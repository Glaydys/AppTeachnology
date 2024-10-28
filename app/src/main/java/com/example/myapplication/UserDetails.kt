package com.example.myapplication

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Retrofit.ApiService
import com.example.myapplication.Retrofit.products
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserDetails : AppCompatActivity() {
    private lateinit var productRecyclerView: RecyclerView
    private val BASE_URL = "http://$IP_ADDRESS:3003/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_details)

        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "User")
        val textViewUsername: TextView = findViewById(R.id.username)
        textViewUsername.text = "$username"

        productRecyclerView = findViewById(R.id.productRecyclerView)
        productRecyclerView.layoutManager = GridLayoutManager(this, 2)

        fetchProducts()

        // Set user image based on login status
        val userImg: ImageView = findViewById(R.id.userimg)
        updateUserImage(userImg)
    }

    private fun updateUserImage(userImg: ImageView) {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        // Change image based on login status
        if (isLoggedIn) {
            userImg.setImageResource(R.drawable.user2) // Change to user2 image
        } else {
            userImg.setImageResource(R.drawable.user1) // Default to user1 image
        }
    }
    private fun fetchProducts() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)
        api.getProducts().enqueue(object : Callback<List<products>> {
            override fun onResponse(call: Call<List<products>>, response: Response<List<products>>) {
                if (response.isSuccessful) {
                    response.body()?.let { allProducts ->
                        Log.d(TAG, "Fetched products: $allProducts")
                        showProducts(allProducts)
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
    private fun showProducts(products: List<products>) {
        val productAdapter = ProductAdapter(products) { product ->
            Log.d(TAG, "Selected Product: $product")

            val intent = Intent(this, ProductDetails::class.java)
            intent.putExtra("product", product)
            startActivity(intent)
        }
        productRecyclerView.adapter = productAdapter
    }
}
