package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Retrofit.ApiService
import com.example.myapplication.Retrofit.Order
import com.example.myapplication.Retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private val apiService: ApiService by lazy { RetrofitClient.apiService }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        recyclerView = findViewById(R.id.recyclerViewOrderHistory)
        recyclerView.layoutManager = LinearLayoutManager(this)

        orderAdapter = OrderAdapter(emptyList()) { order ->
        }
        recyclerView.adapter = orderAdapter

        val orderStatus = intent.getStringExtra("orderStatus")
        val userId = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE).getString("_id", null)

        if (userId.isNullOrEmpty()) {
            Toast.makeText(
                this,
                "Không tìm thấy userId. Vui lòng đăng nhập lại.",
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        getOrderHistory(userId, orderStatus)
    }

    private fun getOrderHistory(userId: String, status: String?) {
        apiService.getOrders(userId, status).enqueue(object : Callback<List<Order>> {
            override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                if (response.isSuccessful) {
                    val orders = response.body()
                    if (!orders.isNullOrEmpty()) {
                        orderAdapter.updateData(orders)
                    } else {
                        Toast.makeText(
                            this@OrderHistoryActivity,
                            "Không có đơn hàng",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@OrderHistoryActivity,
                        "Lỗi khi tải đơn hàng",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                Toast.makeText(
                    this@OrderHistoryActivity,
                    "Lỗi mạng: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
