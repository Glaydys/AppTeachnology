package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class UserInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        // Lấy thông tin người dùng từ SharedPreferences
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "User")
        val email = sharedPreferences.getString("email", "user@example.com")

        // Hiển thị thông tin người dùng
        val textViewUsername: TextView = findViewById(R.id.textViewUsername)
        val textViewEmail: TextView = findViewById(R.id.textViewEmail)

        textViewUsername.text = "Username: $username"
        textViewEmail.text = "Email: $email"
    }
}