package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Retrofit.ApiService
import com.example.myapplication.Retrofit.CartResponse
import com.example.myapplication.Retrofit.PaymentRequest
import com.example.myapplication.Retrofit.PaymentResponse
import com.example.myapplication.Retrofit.ProductInCart
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.NumberFormat

class CartActivity : AppCompatActivity() {

    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var selectAllCheckBox: CheckBox
    private lateinit var totalPriceTextView: TextView
    private val TAG = "CartActivity"
    private var isSelectAll = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cart)

        cartRecyclerView = findViewById(R.id.recyclerViewCart)
        selectAllCheckBox = findViewById(R.id.selectAllCheckbox)
        totalPriceTextView = findViewById(R.id.totalPrice)

        cartRecyclerView.layoutManager = LinearLayoutManager(this)

        fetchCart()

        // Handle Select All checkbox
        selectAllCheckBox.setOnCheckedChangeListener { _, isChecked ->
            isSelectAll = isChecked
            cartAdapter.updateSelectAll(isSelectAll)
        }

        val btn_back: ImageButton = findViewById(R.id.back)
        btn_back.setOnClickListener {
            onBackPressed()
        }

        val btnCheckout: Button = findViewById(R.id.btnCheckout)
        btnCheckout.setOnClickListener {
            val selectedProducts = getSelectedProducts()

            if (selectedProducts.isNotEmpty()) {

                // Retrieve userId from SharedPreferences
                val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                val userId = sharedPreferences.getString("_id", null)

                if (userId != null) {
                    // Retrieve amount from SharedPreferences
                    val share = getSharedPreferences("Cart", Context.MODE_PRIVATE)
                    val amount = share.getInt("amount", 0)

                    // Create the PaymentRequest with userId
                    val request = PaymentRequest(
                        userId = userId,  // Pass userId here
                        products = selectedProducts,
                        amount = amount,
                        bankCode = "",
                        language = "vn"
                    )

                    // Set up Retrofit and API call
                    val retrofit = Retrofit.Builder()
                        .baseUrl("http://$IP_ADDRESS:3003/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()

                    val api = retrofit.create(ApiService::class.java)

                    // Send API request to create payment URL
                    api.createPaymentUrl(request).enqueue(object : Callback<PaymentResponse> {
                        override fun onResponse(call: Call<PaymentResponse>, response: Response<PaymentResponse>) {
                            if (response.isSuccessful && response.body() != null) {
                                val paymentUrl = response.body()!!.paymentUrl

                                val intent = Intent(this@CartActivity, PaymentActivity::class.java)
                                intent.putExtra("PAYMENT_URL", paymentUrl)
                                startActivity(intent)
                            } else {
                                Toast.makeText(this@CartActivity, "Không thể tạo thanh toán", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<PaymentResponse>, t: Throwable) {
                            Toast.makeText(this@CartActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    Toast.makeText(this, "Người dùng chưa đăng nhập", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Vui lòng chọn sản phẩm để thanh toán", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun getSelectedProducts(): List<ProductInCart> {
        return cartAdapter.cartItems.filter { it.isChecked }
    }

    private fun fetchCart() {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("_id", null)

        if (userId != null) {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://$IP_ADDRESS:3003/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val api = retrofit.create(ApiService::class.java)

            api.getCart(userId).enqueue(object : Callback<CartResponse> {
                override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
                    if (response.isSuccessful) {
                        val cartItems = response.body()?.products ?: listOf()

                        cartAdapter = CartAdapter(cartItems.toMutableList(), userId, isSelectAll) { totalAmount ->
                            updateTotalAmount(totalAmount)  // Update the total amount
                        }

                        //số lượng product của user
                        val cartItemCount = cartItems.sumOf { it.quantity }
                        // Lưu số lượng vào SharedPreferences
                        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                        sharedPreferences.edit().putInt("cartItemCount", cartItemCount).apply()


                        cartRecyclerView.adapter = cartAdapter
                    } else {
                        Log.e(TAG, "Failed to fetch cart items: ${response.code()} ${response.message()}")
                        Toast.makeText(applicationContext, "Failed to load cart", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                    Log.e(TAG, "Error fetching cart items", t)
                    Toast.makeText(applicationContext, "Error loading cart", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "Vui lòng đăng nhập để xem giỏ hàng", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTotalAmount(totalAmount: Double = 0.0) {
        updateTotalPrice(totalAmount)
    }

    private fun updateTotalPrice(totalAmount: Double) {
//        val formattedPrice = NumberFormat.getInstance(Locale("vi", "VN")).format(totalAmount) + " VNĐ"
        val formattedPrice = NumberFormat.getInstance().format(totalAmount)
        totalPriceTextView.text = "Tổng tiền: $formattedPrice VNĐ"

        // Lấy số tiền từ chuỗi và loại bỏ dấu phân cách (nếu có)
        val amount = formattedPrice.replace(",", "").toDouble().toInt()
        val sharedPreferences = getSharedPreferences("Cart", Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt("amount",amount).apply()

    }
}

