package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication.Retrofit.Rate
import com.example.myapplication.Retrofit.RetrofitClient
import java.text.NumberFormat
import java.util.Locale
import com.example.myapplication.Retrofit.products
import retrofit2.Call
import retrofit2.Response

class ProductDetails : AppCompatActivity() {

    private lateinit var productTitle: TextView
    private lateinit var productPrice: TextView
    private lateinit var productImage: ImageView
    private lateinit var imgProductImage: ImageView
    private lateinit var txtProductDescription : TextView

    private val TAG = "ProductDetails"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_details)

        productTitle = findViewById(R.id.productTitle)
        productImage = findViewById(R.id.productImage)
        productPrice = findViewById(R.id.productPrice)
        imgProductImage = findViewById(R.id.imgProductImage)
        txtProductDescription  = findViewById(R.id.txtProductDescription)
        val productRating: TextView = findViewById(R.id.productRating)
        val totaluser: TextView = findViewById(R.id.totaluser)

        // chuyen sang searchactivity
        val tv_search: TextView = findViewById(R.id.search_bar)
        tv_search.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var intent = Intent(this@ProductDetails, SearchActivity::class.java)
                startActivity(intent)
            }
        })
        // Get product information from intent
        val product = intent.getParcelableExtra<products>("product")

        if (product != null) {
            Log.d(TAG, "Product received: $product")
            productTitle.text = product.name_product
            txtProductDescription.text = product.description

            val price = product.price.toLongOrNull()
            if (price != null) {
                val formattedPrice = NumberFormat.getInstance(Locale("vi", "VN")).format(price) + " VNĐ"
                productPrice.text = formattedPrice
            } else {
                productPrice.text = product.price
            }
            productRating.text = product.rate
            if (product.totalUserRate !== 0) {
                totaluser.text = "(${product.totalUserRate})"
            } else {
                totaluser.text = ""; // Hoặc giá trị mặc định như "Không có đánh giá"
            }

            Glide.with(this)
                .load("http://$IP_ADDRESS:3000/${product.image_product}")
                .into(productImage)

            Glide.with(this)
                .load("http://$IP_ADDRESS:3000/${product.image_product}")
                .into(imgProductImage)

        } else {
            Log.e(TAG, "No product data received")
        }

        val btnCancel: Button = findViewById(R.id.btnCancel)
        val btnSubmit: Button = findViewById(R.id.btnSubmit)
        val formLayout: LinearLayout = findViewById(R.id.formLayout)
        val ratingBar = findViewById<RatingBar>(R.id.ratingBar)
        val imgProductReviews: ImageButton = findViewById(R.id.imgProductReviews)
        imgProductReviews.setOnClickListener {
            val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

            if (isLoggedIn) {
                val uer_id = sharedPreferences.getString("_id", null)
//                Toast.makeText(this, "Da login voi id: $uer_id va product id: ${product?._id}", Toast.LENGTH_LONG).show()
                formLayout.setVisibility(View.VISIBLE)

                ratingBar.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { _, rating, _ ->
//                    Toast.makeText(this, "Rating: $rating", Toast.LENGTH_LONG).show()
                }

                btnSubmit.setOnClickListener{
                    val rating = ratingBar.rating

                    val productId = product?._id.toString()
                    val userId = uer_id.toString()
                    val rateNumber = rating.toFloat()

                    val rate = Rate(productId, userId, rateNumber)

                    RetrofitClient.apiService.addRate(rate).enqueue(object : retrofit2.Callback<Rate> {
                        override fun onResponse(call: Call<Rate>, response: Response<Rate>) {
                            if (response.isSuccessful) {
                                Log.e(TAG, "Rating: $rating, id: $uer_id va product id: ${product?._id} ")
                                Toast.makeText(this@ProductDetails, "Bạn đã đánh giá thành công", Toast.LENGTH_LONG).show()
                                formLayout.setVisibility(View.GONE)
                            }
                        }

                        override fun onFailure(call: Call<Rate>, t: Throwable) {
                            Toast.makeText(this@ProductDetails, "Lỗi: ${t.message}", Toast.LENGTH_LONG).show()
                            Log.e("Log", "Error: ", t)
                        }
                    })
                }

            } else {
                Toast.makeText(this, "Chua login", Toast.LENGTH_LONG).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        // Tạo màu vàng
        val colorStateList = ColorStateList.valueOf(Color.parseColor("#FFEB3B"))

        // Áp dụng màu vàng cho RatingBar
        ratingBar.progressTintList = colorStateList
        ratingBar.secondaryProgressTintList = colorStateList
        ratingBar.backgroundTintList = colorStateList


        btnCancel.setOnClickListener{
            formLayout.setVisibility(View.GONE)
        }
    }
}
