package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.Locale
import com.example.myapplication.Retrofit.products
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

            Glide.with(this)
                .load("http://$IP_ADDRESS:3000/${product.image_product}")
                .into(productImage)

            Glide.with(this)
                .load("http://$IP_ADDRESS:3000/${product.image_product}")
                .into(imgProductImage)

        } else {
            Log.e(TAG, "No product data received")
        }
    }
}
