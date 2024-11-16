package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Retrofit.ApiService
import com.example.myapplication.Retrofit.CartResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.NumberFormat
import java.util.Locale
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
        val formattedPrice = NumberFormat.getInstance(Locale("vi", "VN")).format(totalAmount) + " VNĐ"
        totalPriceTextView.text = "Tổng tiền: $formattedPrice"
    }
}
