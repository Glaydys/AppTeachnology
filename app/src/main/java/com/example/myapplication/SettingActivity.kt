package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class SettingActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setting)

        val nameTextView: TextView = findViewById(R.id.name)
        val emailTextView: TextView = findViewById(R.id.email)
        val addressTextView: TextView = findViewById(R.id.address)

        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "Không có tên")
        val email = sharedPreferences.getString("email", "Không có email")
        val address = sharedPreferences.getString("address", "Không có địa chỉ")

        // Hiển thị thông tin người dùng
        nameTextView.text = "Tên: $username"
        emailTextView.text = "Email: $email"
        addressTextView.text = "Địa chỉ: $address"


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Xử lý sự kiện nhấn "Đăng xuất"
        val logoutTextView: TextView = findViewById(R.id.logoutTextView)
        logoutTextView.setOnClickListener {
            // Xóa trạng thái đăng nhập
            val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()  // Xóa hết thông tin đã lưu
            editor.apply()

            // Chuyển hướng về LoginActivity
            val intent = Intent(this@SettingActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

}