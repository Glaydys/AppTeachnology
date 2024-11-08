package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
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

        // Set user image based on login status
        val userImg: ImageView = findViewById(R.id.userimg)
        updateUserImage(userImg)

        val settingsIcon: ImageView = findViewById(R.id.settings_icon)
        settingsIcon.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
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

}
