package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.Retrofit.RegisterResponse
import com.example.myapplication.Retrofit.RetrofitClient
import com.example.myapplication.Retrofit.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import android.content.Intent // Nhớ thêm import cho Intent

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val buttonRegister: Button = findViewById(R.id.buttonRegister)
        val editTextUsername: EditText = findViewById(R.id.editTextUsername)
        val editTextPassword: EditText = findViewById(R.id.editTextPassword)
        val editTextEmail: EditText = findViewById(R.id.editTextEmail)
        val editTextAddress: EditText = findViewById(R.id.editTextAddress)

        buttonRegister.setOnClickListener {
            // Xử lý đăng ký
            val username = editTextUsername.text.toString()
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            val address = editTextAddress.text.toString()

            val user = User(username, email, password,address)

            RetrofitClient.apiService.register(user).enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    if (response.isSuccessful) {
                        val username = user.username
                        Toast.makeText(this@RegisterActivity, "Đăng ký thành công $username!", Toast.LENGTH_LONG).show()

                        // Chuyển đến trang đăng nhập sau khi đăng ký thành công
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish() // Kết thúc hoạt động này để không quay lại được
                    } else {
                        Toast.makeText(this@RegisterActivity, "Lỗi: ${response.message()}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, "Lỗi mạng: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}