package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.Retrofit.RetrofitClient
import com.example.myapplication.Retrofit.Cart
import com.example.myapplication.Retrofit.products
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.myapplication.Retrofit.Rate
import com.example.myapplication.Retrofit.RatingResponse
import com.example.myapplication.Retrofit.User_comment
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProductDetails : AppCompatActivity() {

    private lateinit var productTitle: TextView
    private lateinit var productPrice: TextView
    private lateinit var productImage: ImageView
    private lateinit var imgProductImage: ImageView
    private lateinit var txtProductDescription: TextView
    private val TAG = "ProductDetails"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_details)

        productTitle = findViewById(R.id.productTitle)
        productImage = findViewById(R.id.productImage)
        productPrice = findViewById(R.id.productPrice)
        imgProductImage = findViewById(R.id.imgProductImage)
        txtProductDescription = findViewById(R.id.txtProductDescription)
        txtProductDescription  = findViewById(R.id.txtProductDescription)
        val productRating: TextView = findViewById(R.id.productRating)
        val totaluser: TextView = findViewById(R.id.totaluser)
        val edt_comment: EditText = findViewById(R.id.edt_comment)

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
                product.price
            }
            productRating.text = product.rate + "/5"
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
                    val comment = edt_comment.text.toString()
                    val rateNumber = rating.toFloat()

                    val rate = Rate(userId, productId, comment, rateNumber)

                    RetrofitClient.apiService.addRate(rate).enqueue(object : retrofit2.Callback<Rate> {
                        override fun onResponse(call: Call<Rate>, response: Response<Rate>) {
                            if (response.isSuccessful) {
                                Log.e(TAG, "Rating: $rating, id: $uer_id va product id: ${product?._id} ")
                                Toast.makeText(this@ProductDetails, "Bạn đã đánh giá thành công", Toast.LENGTH_LONG).show()
                                formLayout.setVisibility(View.GONE)
                            }else{
                                Log.e(TAG, "Rating: $rating, id: $uer_id va product id: ${product?._id} ")
                                Toast.makeText(this@ProductDetails, "Bạn đã đánh giá rồi", Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onFailure(call: Call<Rate>, t: Throwable) {
                            Toast.makeText(this@ProductDetails, "Bạn đã đánh giá rồi", Toast.LENGTH_LONG).show()
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

        val userImg: ImageView = findViewById(R.id.user_login)
        userImg.setOnClickListener {
            val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

            if (isLoggedIn) {
                val intent = Intent(this, UserDetails::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        val cart: ImageView = findViewById(R.id.carts)
        cart.setOnClickListener {
            val SharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val userId = SharedPreferences.getString("_id", null)
            if(userId == null) {
                val intent = Intent(this@ProductDetails, LoginActivity::class.java)
                startActivity(intent)
            }else{
                val intent = Intent(this@ProductDetails, CartActivity::class.java)
                startActivity(intent)
            }
        }

        updateUserImage(userImg)

        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("_id", null)

        if (userId != null) {
            val buynowButton: Button = findViewById(R.id.buynow)
            buynowButton.setOnClickListener {
                val productId = product?._id ?: run {
                    Log.e(TAG, "Product ID is null")
                    Toast.makeText(this, "Product ID không hợp lệ", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val quantity = 1

                Log.d(TAG, "User ID: $userId")
                Log.d(TAG, "Product ID: $productId")

                val cartItem = Cart(userId, productId, quantity)

                // Retrofit API call to add product to the cart
                RetrofitClient.apiService.addtoCart(cartItem).enqueue(object : Callback<Cart> {
                    override fun onResponse(call: Call<Cart>, response: Response<Cart>) {
                        if (response.isSuccessful) {
                            Log.d(TAG, "Added to cart successfully: ${response.body()}")
                            Toast.makeText(this@ProductDetails, "Thêm vào giỏ hàng thành công", Toast.LENGTH_SHORT).show()

                            // After adding to the cart, navigate to CartActivity to show cart items
                            val intent = Intent(this@ProductDetails, CartActivity::class.java)
                            startActivity(intent)  // Open CartActivity
                        } else {
                            Log.e(TAG, "Failed to add to cart: ${response.code()} ${response.message()}")
                            Toast.makeText(this@ProductDetails, "Không thể thêm vào giỏ hàng", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Cart>, t: Throwable) {
                        Log.e(TAG, "Error adding to cart: ${t.message}")
                        Toast.makeText(this@ProductDetails, "Lỗi khi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        } else {
            Log.e(TAG, "User ID is null; ensure user is logged in")
            Toast.makeText(this, "Vui lòng đăng nhập để mua hàng", Toast.LENGTH_SHORT).show()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewComment)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val productId = product?._id.toString()

        RetrofitClient.apiService.getComment(productId).enqueue(object : Callback<RatingResponse> {
            override fun onResponse(
                call: Call<RatingResponse>,
                response: Response<RatingResponse>
            ) {
                if (response.isSuccessful) {
                    val ratingResponse = response.body()
                    if (ratingResponse != null) {
                        val usersList = ratingResponse.users // Lấy danh sách người dùng
                        val adapter = CommentAdapter(usersList) // Pass danh sách người dùng vào adapter
                        recyclerView.adapter = adapter
                    }
                } else {
                    Log.e(TAG, "Error: ${response.message()}")
                    Toast.makeText(this@ProductDetails, "Lỗi response", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(
                call: Call<RatingResponse>,
                t: Throwable
            ) {
                Log.e(TAG, "Error fetching comment: ${t.message}")
                Toast.makeText(this@ProductDetails, "Failed to fetch comments", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun updateUserImage(userImg: ImageView) {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        userImg.setImageResource(if (isLoggedIn) R.drawable.user2 else R.drawable.user)
    }
}

class CommentAdapter(private val comments: List<User_comment>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate_comment)
        val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        val tvComment: TextView = itemView.findViewById(R.id.tvComment)
        val tvRating: RatingBar = itemView.findViewById(R.id.ratingBarComment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.info_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val user = comments[position]

        // Chuyển đổi từ String sang Date
        val inputDate = user.createdAt.toString()
        val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        val outputFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        try {
            val date: Date = inputFormatter.parse(inputDate)!! // Chuyển đổi từ String sang Date
            val formattedDate: String = outputFormatter.format(date) // Định dạng lại ngày
            holder.tvDate.text = formattedDate
        } catch (e: Exception) {
            e.printStackTrace()
            holder.tvDate.text = "Invalid date"
        }

        holder.tvUsername.text = user.userId.username // Lấy tên người dùng
        holder.tvComment.text = user.comment // Lấy nội dung bình luận
        holder.tvRating.rating = user.rate.toFloat() // Chuyển đổi từ Int sang Float cho RatingBar

        // Lấy progressDrawable và áp dụng màu vàng
        val drawable = holder.tvRating.progressDrawable
        if (drawable is LayerDrawable) {
            val background = drawable.getDrawable(0) // Lớp nền (sao chưa được chọn)
            val secondary = drawable.getDrawable(1) // Lớp phần sao được chọn một phần
            val progress = drawable.getDrawable(2) // Lớp phần sao được chọn đầy đủ

            val yellow = Color.parseColor("#FFEB3B")
            val gray = Color.parseColor("#CCCCCC")

            background.setColorFilter(gray, PorterDuff.Mode.SRC_ATOP) // Màu xám cho nền
            secondary.setColorFilter(yellow, PorterDuff.Mode.SRC_ATOP) // Màu vàng nhạt cho phần chọn một phần
            progress.setColorFilter(yellow, PorterDuff.Mode.SRC_ATOP) // Màu vàng cho phần chọn đầy đủ
        }
    }

    override fun getItemCount(): Int {
        return comments.size
    }
}
