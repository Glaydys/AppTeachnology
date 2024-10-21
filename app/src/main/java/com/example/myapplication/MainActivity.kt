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
    private val BASE_URL = "http://192.168.1.12:3003/" // Ensure correct IP and port
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
            recyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
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
            override fun onResponse(
                call: Call<List<category>>,
                response: Response<List<category>>
            ) {
                if (response.isSuccessful) {
                    val categories = response.body() ?: emptyList()
                    categoryRecyclerView.adapter = CategoryAdapter(categories)

                    // Gắn adapter cho categoryRecyclerView với onCategoryClick
                    categoryRecyclerView.adapter = CategoryAdapter(categories).apply {
                        onCategoryClick = { selectedCategory ->
                            // Khi người dùng click vào danh mục, chuyển qua ProductDisplayActivity
                            val intent =
                                Intent(this@MainActivity, ProductDisplayActivity::class.java)
                            intent.putExtra("category_id", selectedCategory.category_id)
                            intent.putExtra("category_name", selectedCategory.name_category)
                            startActivity(intent)
                        }
                    }
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

    // Định nghĩa hàm fetchProductsByCategory
    private fun fetchProductsByCategory(categoryId: Int, recyclerViewIndex: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(MyApi::class.java)
//end
        api.getProducts().enqueue(object : Callback<List<products>> {
            override fun onResponse(call: Call<List<products>>, response: Response<List<products>>) {
                if (response.isSuccessful) {
                    response.body()?.let { allProducts ->
                        Log.d(TAG, "Fetched products: $allProducts")
                        // Filter products by categoryId
                        val filteredProducts = allProducts.filter { it.category_id == categoryId }
                        showProducts(filteredProducts, recyclerViewIndex)
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
        if (recyclerViewIndex >= 0 && recyclerViewIndex < productRecyclerViews.size) {
            val productAdapter = ProductAdapter(products) { product ->
                Log.d(TAG, "Selected Product: $product")

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
