package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication.Retrofit.RetrofitClient
import com.example.myapplication.Retrofit.Cart
import com.example.myapplication.Retrofit.products
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class ProductDetails : AppCompatActivity() {

    private lateinit var productTitle: TextView
    private lateinit var productPrice: TextView
    private lateinit var productImage: ImageView
    private lateinit var imgProductImage: ImageView
    private lateinit var txtProductDescription: TextView
    private val TAG = "ProductDetails"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_details)

        productTitle = findViewById(R.id.productTitle)
        productImage = findViewById(R.id.productImage)
        productPrice = findViewById(R.id.productPrice)
        imgProductImage = findViewById(R.id.imgProductImage)
        txtProductDescription = findViewById(R.id.txtProductDescription)

        // Lấy thông tin sản phẩm từ intent
        val product = intent.getParcelableExtra<products>("product")

        if (product != null) {
            Log.d(TAG, "Product received: $product")
            productTitle.text = product.name_product
            txtProductDescription.text = product.description

            val price = product.price.toLongOrNull()
            productPrice.text = if (price != null) {
                NumberFormat.getInstance(Locale("vi", "VN")).format(price) + " VNĐ"
            } else {
                product.price
            }

            Glide.with(this)
                .load("http://$IP_ADDRESS:3000/${product.image_product}")
                .into(productImage)

            Glide.with(this)
                .load("http://$IP_ADDRESS:3000/${product.image_product}")
                .into(imgProductImage)
        } else {
            Log.e(TAG, "No product data received")
        }
        val userImg: ImageView = findViewById(R.id.user_login)
        userImg.setOnClickListener {
            val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

            if (isLoggedIn) {
                val intent = Intent(this, UserDetails::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        val cart: ImageView = findViewById(R.id.carts)
        cart.setOnClickListener {
            val SharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val userId = SharedPreferences.getString("userId", null)
            if(userId == null) {
                val intent = Intent(this@ProductDetails, LoginActivity::class.java)
                startActivity(intent)
            }else{
                val intent = Intent(this@ProductDetails, CartActivity::class.java)
                startActivity(intent)
            }
        }

        updateUserImage(userImg)

        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null)

        if (userId != null) {
            val buynowButton: Button = findViewById(R.id.buynow)
            buynowButton.setOnClickListener {
                val productId = product?._id ?: run {
                    Log.e(TAG, "Product ID is null")
                    Toast.makeText(this, "Product ID không hợp lệ", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val quantity = 1

                Log.d(TAG, "User ID: $userId")
                Log.d(TAG, "Product ID: $productId")

                val cartItem = Cart(userId, productId, quantity)

                // Retrofit API call to add product to the cart
                RetrofitClient.apiService.addtoCart(cartItem).enqueue(object : Callback<Cart> {
                    override fun onResponse(call: Call<Cart>, response: Response<Cart>) {
                        if (response.isSuccessful) {
                            Log.d(TAG, "Added to cart successfully: ${response.body()}")
                            Toast.makeText(this@ProductDetails, "Thêm vào giỏ hàng thành công", Toast.LENGTH_SHORT).show()

                            // After adding to the cart, navigate to CartActivity to show cart items
                            val intent = Intent(this@ProductDetails, CartActivity::class.java)
                            startActivity(intent)  // Open CartActivity
                        } else {
                            Log.e(TAG, "Failed to add to cart: ${response.code()} ${response.message()}")
                            Toast.makeText(this@ProductDetails, "Không thể thêm vào giỏ hàng", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Cart>, t: Throwable) {
                        Log.e(TAG, "Error adding to cart: ${t.message}")
                        Toast.makeText(this@ProductDetails, "Lỗi khi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        } else {
            Log.e(TAG, "User ID is null; ensure user is logged in")
            Toast.makeText(this, "Vui lòng đăng nhập để mua hàng", Toast.LENGTH_SHORT).show()
        }
    }
    private fun updateUserImage(userImg: ImageView) {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        userImg.setImageResource(if (isLoggedIn) R.drawable.user2 else R.drawable.user)
    }
}
