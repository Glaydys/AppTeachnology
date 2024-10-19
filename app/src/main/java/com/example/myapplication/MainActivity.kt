package com.example.myapplication

import android.content.Intent
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

class MainActivity : AppCompatActivity() {

    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var productRecyclerView: RecyclerView
    private val BASE_URL = "http://192.168.2.22:3003/" // Ensure correct IP and port
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.trangchu)

        categoryRecyclerView = findViewById(R.id.categoryRecyclerView)
        categoryRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        productRecyclerView = findViewById(R.id.productRecyclerView)
        productRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Fetch categories
        fetchCategories()

        val categoriesFromDb = listOf(
            category(1, "Điện thoại", R.drawable.img1),
            category(2, "Máy tính bảng", R.drawable.img2),
            category(3, "Laptop", R.drawable.img3),
            category(4, "Đồng hồ", R.drawable.img4),
            category(5, "Máy tính để bàn", R.drawable.img5),
            category(6, "Máy in", R.drawable.img6),
            category(7, "Máy ảnh", R.drawable.img7)
        )
        categoryRecyclerView.adapter = CategoryAdapter(categoriesFromDb)
        fetchProductsByCategory(5) // Changed to fetch category 5
    }

    private fun fetchCategories() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(MyApi::class.java)

        api.getCategories().enqueue(object : Callback<List<category>> {
            override fun onResponse(call: Call<List<category>>, response: Response<List<category>>) {
                if (response.isSuccessful) {
                    val categories = response.body() ?: emptyList()
                    categoryRecyclerView.adapter = CategoryAdapter(categories)
                } else {
                    Log.e(TAG, "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<category>>, t: Throwable) {
                Log.e(TAG, "Failed to fetch categories: ${t.message}")
            }
        })
    }

    private fun fetchProductsByCategory(categoryId: Int) {
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MyApi::class.java)

        // Fetch products from API
        api.getProducts().enqueue(object : Callback<List<products>> {
            override fun onResponse(call: Call<List<products>>, response: Response<List<products>>) {
                if (response.isSuccessful) {
                    response.body()?.let { allProducts ->
                        Log.d(TAG, "Fetched products: $allProducts") // Log fetched products
                        // Filter products by categoryId
                        val filteredProducts = allProducts.filter { it.category_id.toString() == categoryId.toString() }
                        showProducts(filteredProducts)
                    } ?: Log.e(TAG, "No products found")
                } else {
                    Log.e(TAG, "Error fetching products: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<products>>, t: Throwable) {
                Log.e(TAG, "Failed to fetch products: ${t.message}")
            }
        })
    }

    // Display products in RecyclerView
    private fun showProducts(products: List<products>) {
        if (products.isEmpty()) {
            Log.e(TAG, "No products available for the selected category")
            return
        }

        productRecyclerView.layoutManager = LinearLayoutManager(this)
        val productAdapter = ProductAdapter(products) { product ->
            Log.d(TAG, "Selected Product: $product") // Log selected product
            // Start ProductDetailsActivity when a product is clicked
            val intent = Intent(this, ProductDetails::class.java)
            intent.putExtra("product", product) // Pass product data
            startActivity(intent)
        }
        productRecyclerView.adapter = productAdapter
    }
}
