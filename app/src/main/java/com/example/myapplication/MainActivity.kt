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
    private lateinit var productRecyclerViews: List<RecyclerView>
    private val BASE_URL = "http://192.168.2.22:3003/" // Ensure correct IP and port
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.trangchu)

        categoryRecyclerView = findViewById(R.id.categoryRecyclerView)
        categoryRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // list of RecyclerViews for products
        productRecyclerViews = listOf(
            findViewById(R.id.productRecyclerView),
            findViewById(R.id.productRecyclerView2),
            findViewById(R.id.productRecyclerView3),
            findViewById(R.id.productRecyclerView4),
            findViewById(R.id.productRecyclerView5),
            findViewById(R.id.productRecyclerView6),
            findViewById(R.id.productRecyclerView7)
        )

        // LayoutManager for each RecyclerView
        productRecyclerViews.forEach { recyclerView ->
            recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        }

        // Fetch categories
        fetchCategories()
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

                    // Fetch products for each category
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
                        // products by categoryId
                        val filteredProducts = allProducts.filter { it.category_id == categoryId }
                        showProducts(filteredProducts, recyclerViewIndex) // Show products in the corresponding RecyclerView
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
    private fun showProducts(products: List<products>, recyclerViewIndex: Int) {
        // Check if the products list is empty
        if (products.isEmpty()) {
            Log.e(TAG, "No products available for the selected category")
            return
        }

        // Initialize product adapter
        val productAdapter = ProductAdapter(products) { product ->
            Log.d(TAG, "Selected Product: $product") // Log selected product
            // Start ProductDetailsActivity when a product is clicked
            val intent = Intent(this, ProductDetails::class.java)
            intent.putExtra("product", product) // Pass product data
            startActivity(intent)
        }

        if (recyclerViewIndex >= 0 && recyclerViewIndex < productRecyclerViews.size) {
            productRecyclerViews[recyclerViewIndex].adapter = productAdapter // Update adapter for the corresponding RecyclerView
        } else {
            Log.e(TAG, "Invalid recyclerViewIndex: $recyclerViewIndex")
        }
    }

}
