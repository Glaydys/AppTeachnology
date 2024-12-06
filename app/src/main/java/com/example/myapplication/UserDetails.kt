package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class UserDetails : AppCompatActivity() {
    private val BASE_URL = "http://$IP_ADDRESS:3003/"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_details)

        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "User")
        val textViewUsername: TextView = findViewById(R.id.username)
        textViewUsername.text = "$username"

        // Cập nhật ảnh người dùng dựa trên trạng thái đăng nhập
        val userImg: ImageView = findViewById(R.id.userimg)
        updateUserImage(userImg)

        val settingsIcon: ImageView = findViewById(R.id.settings_icon)
        settingsIcon.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        // Xử lý nhấp cho từng trạng thái đơn hàng
        findViewById<LinearLayout>(R.id.status_all).setOnClickListener {
            openOrderHistoryByStatus(null) // Truyền null để hiển thị tất cả đơn hàng
        }
        findViewById<LinearLayout>(R.id.status_waiting_confirmation).setOnClickListener {
            openOrderHistoryByStatus("Đang chờ xác nhận")
        }
        findViewById<LinearLayout>(R.id.status_waiting_delivery).setOnClickListener {
            openOrderHistoryByStatus("Đang giao")
        }
        findViewById<LinearLayout>(R.id.status_review).setOnClickListener {
            openOrderHistoryByStatus("Đã giao")
        }
    }
    private fun updateUserImage(userImg: ImageView) {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        // Thay đổi hình ảnh dựa trên trạng thái đăng nhập
        if (isLoggedIn) {
            userImg.setImageResource(R.drawable.user2) // Thay đổi thành ảnh user2
        } else {
            userImg.setImageResource(R.drawable.user1) // Mặc định là ảnh user1
        }
    }

    private fun openOrderHistoryByStatus(status: String?) {
        val intent = Intent(this, OrderHistoryActivity::class.java)
        intent.putExtra("orderStatus", status) // Nếu null, sẽ hiển thị tất cả đơn hàng
        startActivity(intent)
    }

}
