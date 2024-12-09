package com.example.myapplication

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Retrofit.ApiService
import com.example.myapplication.Retrofit.Cart
import com.example.myapplication.Retrofit.CartDelete
import com.example.myapplication.Retrofit.CartResponse
import com.example.myapplication.Retrofit.CartUpdateRequest
import com.example.myapplication.Retrofit.ProductInCart
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(
    val cartItems: MutableList<ProductInCart>,
    private val userId: String,
    private var isSelectAll: Boolean,
    private val onItemChecked: (Double) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private var totalAmount = 0.0
    private var isUpdating = false

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productQuantity: TextView = itemView.findViewById(R.id.productQuantity)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val selectCheckBox: CheckBox = itemView.findViewById(R.id.selectCheckBox)
        val decreaseButton: Button = itemView.findViewById(R.id.decreaseQuantity)
        val increaseButton: Button = itemView.findViewById(R.id.increaseQuantity)
        val removeProduct: Button = itemView.findViewById(R.id.removeProduct)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        val price = cartItem.productId.price.toDouble()
        val formattedPrice = NumberFormat.getInstance(Locale("vi", "VN")).format(price) + " VNĐ"

        holder.productName.text = cartItem.productId.name_product
        holder.productQuantity.text = cartItem.quantity.toString()
        holder.productPrice.text = formattedPrice
        holder.selectCheckBox.isChecked = cartItem.isChecked

        // Handle checkbox selection
        holder.selectCheckBox.setOnCheckedChangeListener { _, isChecked ->
            cartItem.isChecked = isChecked
            cartItem.productId
            updateTotalAmount()
        }

        holder.decreaseButton.setOnClickListener {
            if (cartItem.quantity > 1 && !isUpdating) {
                isUpdating = false
                cartItem.quantity -= 1
                holder.productQuantity.text = cartItem.quantity.toString()
                updateTotalAmount()
                updateCartInDatabase(cartItem)
            }
        }
        // Handle increase quantity
        holder.increaseButton.setOnClickListener {
            if (!isUpdating) {
                isUpdating = false
                val limit = cartItem.productId.quantity 
                if (cartItem.quantity < limit) {
                    cartItem.quantity += 1
                    holder.productQuantity.text = cartItem.quantity.toString()
                    updateTotalAmount()
                    updateCartInDatabase(cartItem)
                } else {
                    Log.d("CartAdapter", "Sản phẩm đã hết hàng${cartItem.productId.name_product}")
                }
            }

    }

        //delete
        holder.removeProduct.setOnClickListener {
            removeProductFromCart(cartItem,position)
        }
    }

    private fun removeProductFromCart(cartItem: ProductInCart, position: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://$IP_ADDRESS:3003/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)


        val userId = userId
        val productId = cartItem.productId._id
        val cartDelete = CartDelete(userId,productId)
        val call = api.deleteProductFromCart(cartDelete)
        call.enqueue(object : Callback<CartResponse> {
            override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
                if (response.isSuccessful) {
                    Log.d("CartAdapter", "Before removal: position=$position, cartItems=${cartItems.size}")
                    cartItems.removeAt(position)
                    Log.d("CartAdapter", "After removal: cartItems=${cartItems.size}")
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position,cartItems.size)
                    Log.d("CartAdapter", "Product removed successfully: ${response.body()}")
                } else {
                    Log.e("CartAdapter", "Failed to remove product: ${response.code()} ${response.message()}")
                }
            }

            override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                Log.e("CartAdapter", "Error removing product", t)
            }
        })
    }

    private fun updateTotalAmount() {
        totalAmount = 0.0
        for (cartItem in cartItems) {
            if (cartItem.isChecked) {
                totalAmount += cartItem.productId.price.toDouble() * cartItem.quantity
            }
        }
        onItemChecked(totalAmount)
    }

    fun updateSelectAll(isSelectAll: Boolean) {
        this.isSelectAll = isSelectAll
        for (cartItem in cartItems) {
            cartItem.isChecked = isSelectAll
        }
        updateTotalAmount()
        notifyDataSetChanged()
    }

    private fun updateCartInDatabase(cartItem: ProductInCart) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://$IP_ADDRESS:3003/")  // Đúng địa chỉ IP server
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)

        val cartUpdateRequest = CartUpdateRequest(
            userId = userId,
            products = listOf(
                Cart(
                    userId = userId,
                    productId = cartItem.productId._id,
                    quantity = cartItem.quantity
                )
            )
        )

        Log.d("CartAdapter", "Sending update request: $cartUpdateRequest") // Debug dữ liệu request

        api.updateCart(cartUpdateRequest).enqueue(object : Callback<CartResponse> {
            override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
                if (response.isSuccessful) {
                    Log.d("CartAdapter", "Cart updated successfully: ${response.body()}")
                } else {
                    Log.e("CartAdapter", "Failed to update cart: ${response.code()} ${response.message()}")
                }
            }

            override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                Log.e("CartAdapter", "Error updating cart", t)
            }
        })
    }

    override fun getItemCount(): Int = cartItems.size
}