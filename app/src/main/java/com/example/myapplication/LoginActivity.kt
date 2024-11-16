package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.Retrofit.LoginResponse
import com.example.myapplication.Retrofit.RetrofitClient
import com.example.myapplication.Retrofit.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val buttonLogin: Button = findViewById(R.id.buttonLogin)
        val editTextUsername: EditText = findViewById(R.id.editTextUsername)
        val editTextPassword: EditText = findViewById(R.id.editTextPassword)

        buttonLogin.setOnClickListener {
            // Xử lý đăng nhập
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()
            val user = User(username, "", password)

            RetrofitClient.apiService.login(user).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        val userId = loginResponse?._id      // Lấy userId từ phản hồi
                        val username = loginResponse?.username
                        val email = loginResponse?.email

                        Toast.makeText(this@LoginActivity, "Chào mừng $username!", Toast.LENGTH_LONG).show()

                        // Lưu thông tin đăng nhập vào SharedPreferences
                        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                        with (sharedPreferences.edit()) {
                            putBoolean("isLoggedIn", true)
                            putString("userId", userId)       // Lưu userId vào SharedPreferences
                            putString("username", username)
                            putString("email", email)
                            apply()
                        }

                        // Chuyển sang MainActivity
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Lỗi: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Lỗi mạng: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    fun onRegisterClick(view: View) {
        try {
            startActivity(Intent(this, RegisterActivity::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
