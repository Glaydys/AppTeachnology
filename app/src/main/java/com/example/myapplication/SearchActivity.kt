package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Retrofit.ApiService
import com.example.myapplication.Retrofit.products
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    //    private val BASE_URL = "https://jsonplaceholder.typicode.com/"
    private val BASE_URL = "http://"+IP_ADDRESS+":3003/"
    private val TAG: String = "CHECK_RESPONSE"
    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var searchView: SearchView
    private var productList = mutableListOf<products>()
    private var filteredList = mutableListOf<products>()
    private lateinit var noResultsTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search)

        searchView = findViewById(R.id.searchView)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this,2)
        productAdapter = ProductAdapter(filteredList) {product ->
            Log.d(TAG, "Product clicked: $product")

            val intent = Intent(this@SearchActivity, ProductDetails::class.java)
            intent.putExtra("product", product)
            startActivity(intent)
        }   // hien thi san pham
        recyclerView.adapter = productAdapter

        val btn_back: ImageButton = findViewById(R.id.btn_back)
        btn_back.setOnClickListener {
            onBackPressed() // Quay lại màn hình trước
        }

        noResultsTextView = findViewById(R.id.noResultsTextView)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterProducts(newText)
                return true
            }
        })

        getAllProducts()
    }

    private fun getAllProducts(){
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) //CHUYEN DOI RES JSON TU API THANH DOI TƯƠNG LIB.GSON
            .build()
            .create(ApiService::class.java)

        api.getProducts().enqueue(object : Callback<List<products>>{ // ENQUEUE THUC HIEN GOI API BAT DONG BO
            override fun onResponse(
                call: Call<List<products>>,
                response: Response<List<products>>
            ) {
                if (response.isSuccessful){
                    response.body()?.let {
                        for (products in it){
                            Log.i(TAG,"onResponse: ${products}")
                        }
                        productList.clear()
                        productList.addAll(it)
                        filteredList.clear()
                        filteredList.addAll(it)
                        productAdapter.notifyDataSetChanged()
                    }
                }else{
                    Log.e(TAG, "API trả về lỗi: ${response.code()} và lỗi: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(
                call: Call<List<products>>,
                t: Throwable
            ) {
                Log.e(TAG,"onFailure: ${t.message}")
            }

        })

    }
    private fun filterProducts(query: String?) {
        filteredList.clear()
        if (query.isNullOrEmpty()) {
            filteredList.addAll(productList)
        } else {
            val searchQuery = query.lowercase()
            val results = productList.filter {
                it.name_product.lowercase().contains(searchQuery) // Giả sử bạn có thuộc tính name
            }
            filteredList.addAll(results)

            // Kiểm tra nếu không có kết quả
            if (filteredList.isEmpty()) {
                noResultsTextView.visibility = View.VISIBLE // Hiện thông báo
            } else {
                noResultsTextView.visibility = View.GONE // Ẩn thông báo
            }
        }
        productAdapter.notifyDataSetChanged()
    }
}