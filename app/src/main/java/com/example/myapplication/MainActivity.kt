package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.myapplication.Retrofit.category
import com.example.myapplication.Retrofit.products

class MainActivity : AppCompatActivity() {

    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var productRecyclerViews: List<RecyclerView>
    private val BASE_URL = "http://$IP_ADDRESS:3003/"
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.trangchu)

        categoryRecyclerView = findViewById(R.id.categoryRecyclerView)
        categoryRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Gán adapter rỗng ban đầu cho categoryRecyclerView
        categoryRecyclerView.adapter = CategoryAdapter(emptyList())

        productRecyclerViews = listOf(
            findViewById(R.id.productRecyclerView1),
            findViewById(R.id.productRecyclerView2),
            findViewById(R.id.productRecyclerView3),
            findViewById(R.id.productRecyclerView4),
            findViewById(R.id.productRecyclerView5),
            findViewById(R.id.productRecyclerView6),
            findViewById(R.id.productRecyclerView7)
        )

        productRecyclerViews.forEach { recyclerView ->
            recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            // Gán adapter rỗng ban đầu cho từng RecyclerView
            recyclerView.adapter = ProductAdapter(emptyList()) { /* click listener */ }
        }

        // Chuyển sang SearchActivity khi nhấn vào TextView tìm kiếm
        val tv_search: TextView = findViewById(R.id.tv_search)
        tv_search.setOnClickListener {
            val intent = Intent(this@MainActivity, SearchActivity::class.java)
            startActivity(intent)
        }

        fetchCategories()

        // Thiết lập sự kiện click cho ảnh đại diện người dùng
        val userImg: ImageView = findViewById(R.id.user)
        userImg.setOnClickListener {
            val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

            if (isLoggedIn) {
                val intent = Intent(this, UserDetails::class.java)
                startActivity(intent)
            } else { val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        updateUserImage(userImg)
    }

    private fun updateUserImage(userImg: ImageView) {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        userImg.setImageResource(if (isLoggedIn) R.drawable.user2 else R.drawable.user)
    }

    private fun fetchCategories() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)

        api.getCategories().enqueue(object : Callback<List<category>> {
            override fun onResponse(call: Call<List<category>>, response: Response<List<category>>) {
                if (response.isSuccessful) {
                    val categories = response.body() ?: emptyList()
                    categoryRecyclerView.adapter = CategoryAdapter(categories).apply {
                        onCategoryClick = { selectedCategory ->
                            val intent = Intent(this@MainActivity, ProductDisplayActivity::class.java)
                            intent.putExtra("category_id", selectedCategory.category_id)
                            intent.putExtra("category_name", selectedCategory.name_category)
                            startActivity(intent)
                        }
                    }
                    // Gọi hàm lấy sản phẩm cho từng danh mục
                    categories.forEachIndexed { index, category ->
                        fetchProductsByCategory(category.category_id, index)
                    }
                } else {
                    Log.e(TAG, "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<category>>, t: Throwable) {
                Log.e(TAG, "Failed to fetch categories: ${t.message}")
            }
        })
    }

    private fun fetchProductsByCategory(categoryId: Int, recyclerViewIndex: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)

        api.getProducts().enqueue(object : Callback<List<products>> {
            override fun onResponse(call: Call<List<products>>, response: Response<List<products>>) {
                if (response.isSuccessful) {
                    response.body()?.let { allProducts ->
                        val filteredProducts = allProducts.filter { it.category_id == categoryId }
                        showProducts(filteredProducts, recyclerViewIndex)
                    } ?: Log.e(TAG, "No products found")
                } else {
                    Log.e(TAG, "Error fetching products: ${response.code()}") }
            }

            override fun onFailure(call: Call<List<products>>, t: Throwable) {
                Log.e(TAG, "Failed to fetch products: ${t.message}")
            }
        })
    }

    private fun showProducts(products: List<products>, recyclerViewIndex: Int) {
        if (recyclerViewIndex in productRecyclerViews.indices) {
            val productAdapter = ProductAdapter(products) { product ->
                val intent = Intent(this, ProductDetails::class.java)
                intent.putExtra("product", product)
                startActivity(intent)
            }
            productRecyclerViews[recyclerViewIndex].adapter = productAdapter
        } else {
            Log.e(TAG, "Invalid recyclerViewIndex: $recyclerViewIndex")
        }
    }
}