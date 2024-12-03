package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
        val buttonRegister: TextView = findViewById(R.id.buttonRegister)

        buttonRegister.setOnClickListener {
//            Toast.makeText(this@LoginActivity,"register", Toast.LENGTH_LONG).show()
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        buttonLogin.setOnClickListener {
            // Xử lý đăng nhập
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()
            val email = "".toString()
            val user = User(username, email, password)

            RetrofitClient.apiService.login(user).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {

                        val loginResponse = response.body()!!
                        val user = loginResponse.user
                        val username = user?.username // Giả sử bạn có trường username trong LoginResponse
                        val email = user?.email
                        val _id = user?._id

                        Toast.makeText(this@LoginActivity, "Chào mừng $username!", Toast.LENGTH_LONG).show()

                        // Lưu thông tin đăng nhập vào SharedPreferences
                        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("isLoggedIn", true)
                        editor.putString("username", username) // thay thế bằng username thực tế
                        editor.putString("email", email) // thay thế bằng email thực tế
                        editor.putString("_id", _id) // thay thế bằng _id thực tế
                        editor.apply()

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
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
//    fun onRegisterClick() {
//        try {
//            val intent = Intent(this, RegisterActivity::class.java)
//            startActivity(intent)
//        } catch (view: Exception) {
//            view.printStackTrace()
//        }
//    }
}