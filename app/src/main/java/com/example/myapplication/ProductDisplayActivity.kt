package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProductDisplayActivity : AppCompatActivity() {

    private lateinit var productRecyclerView: RecyclerView
    private val BASE_URL = "http://192.168.1.12:3003/"
    private val TAG = "ProductDisplayActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_display)

        productRecyclerView = findViewById(R.id.productRecyclerView)
        productRecyclerView.layoutManager = LinearLayoutManager(this)

        // Lấy category_id từ Intent
        val categoryId = intent.getIntExtra("category_id", -1)

        // Kiểm tra category_id hợp lệ
        if (categoryId != -1) {
            fetchProductsByCategory(categoryId)
        } else {
            Log.e(TAG, "Category ID không hợp lệ")
        }
    }

    private fun fetchProductsByCategory(categoryId: Int) {
        // Tạo Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(MyApi::class.java)

        // Gọi API để lấy sản phẩm theo danh mục
        api.getProducts().enqueue(object : Callback<List<products>> {
            override fun onResponse(call: Call<List<products>>, response: Response<List<products>>) {
                if (response.isSuccessful) {
                    val allProducts = response.body() ?: emptyList()
                    // Lọc sản phẩm theo categoryId
                    val filteredProducts = allProducts.filter { it.category_id == categoryId }
                    showProducts(filteredProducts)
                } else {
                    Log.e(TAG, "Lỗi khi lấy sản phẩm: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<products>>, t: Throwable) {
                Log.e(TAG, "Lỗi gọi API: ${t.message}")
            }
        })
    }

    private fun showProducts(products: List<products>) {
        if (products.isEmpty()) {
            Log.e(TAG, "Không có sản phẩm nào cho danh mục này")
            return
        }

        productRecyclerView.adapter = ProductAdapter(products) { product ->
            Log.d(TAG, "Chọn sản phẩm: $product")
        }
    }
}
