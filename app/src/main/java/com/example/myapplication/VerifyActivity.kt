package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.Retrofit.RetrofitClient
import com.example.myapplication.Retrofit.UserResponse
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class VerifyActivity : AppCompatActivity() {

    private lateinit var input_opt1: EditText
    private lateinit var input_opt2: EditText
    private lateinit var input_opt3: EditText
    private lateinit var input_opt4: EditText
    private lateinit var input_opt5: EditText
    private lateinit var input_opt6: EditText
    private lateinit var resendotp: TextView

    private lateinit var getotpbackend: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)

        val verifyButton: Button = findViewById(R.id.buttonSubmit)

        input_opt1 = findViewById(R.id.input_opt1)
        input_opt2 = findViewById(R.id.input_opt2)
        input_opt3 = findViewById(R.id.input_opt3)
        input_opt4 = findViewById(R.id.input_opt4)
        input_opt5 = findViewById(R.id.input_opt5)
        input_opt6 = findViewById(R.id.input_opt6)

        val textView: TextView = findViewById(R.id.textMobileShowNumber)
        textView.setText(String.format(
            "+84-%s", intent.getStringExtra("mobile")
        ))

        val progressBar: ProgressBar = findViewById(R.id.progressBarVerifyOTP)

        getotpbackend = intent.getStringExtra("backendotp").toString()

        verifyButton.setOnClickListener{
            if(!input_opt1.getText().toString().trim().isEmpty()
                && !input_opt2.getText().toString().trim().isEmpty()
                && !input_opt3.getText().toString().trim().isEmpty()
                && !input_opt4.getText().toString().trim().isEmpty()
                && !input_opt5.getText().toString().trim().isEmpty()
                && !input_opt6.getText().toString().trim().isEmpty()){

                val entercode: String = input_opt1.getText().toString() +
                        input_opt2.getText().toString() +
                        input_opt3.getText().toString() +
                        input_opt4.getText().toString() +
                        input_opt5.getText().toString() +
                        input_opt6.getText().toString()

                if(getotpbackend != null){
                    progressBar.visibility = ProgressBar.VISIBLE
                    verifyButton.visibility = Button.INVISIBLE

                    val phoneAuthCredential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                        getotpbackend, entercode
                    )

                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                        .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                            override fun onComplete(p0: Task<AuthResult>) {
                                progressBar.visibility = ProgressBar.VISIBLE
                                verifyButton.visibility = Button.INVISIBLE

                                if(p0.isSuccessful()){

                                    val username = intent.getStringExtra("mobile").toString()

                                    RetrofitClient.apiService.loginWithOTP(username).enqueue(object : Callback<UserResponse>{
                                        override fun onResponse(
                                            call: Call<UserResponse>,
                                            response: Response<UserResponse>
                                        ) {
                                            if(response.isSuccessful){
                                                val user = response.body()
                                                val username = user?.username
                                                val email = user?.email
                                                val _id = user?._id

                                                Toast.makeText(this@VerifyActivity, "Chào mừng $username!", Toast.LENGTH_LONG).show()

                                                // Lưu thông tin đăng nhập vào SharedPreferences
                                                val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                                                val editor = sharedPreferences.edit()
                                                editor.putBoolean("isLoggedIn", true)
                                                editor.putString("username", username) // thay thế bằng username thực tế
                                                editor.putString("email", email) // thay thế bằng email thực tế
                                                editor.putString("_id", _id) // thay thế bằng _id thực tế
                                                editor.apply()

                                                val intent = Intent(this@VerifyActivity, MainActivity::class.java)
                                                startActivity(intent)
                                                progressBar.visibility = ProgressBar.INVISIBLE
                                                finish()
                                            }else {
                                                Toast.makeText(this@VerifyActivity, "User không tồn tại, vui lòng đăng ký", Toast.LENGTH_LONG).show()
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<UserResponse>,
                                            t: Throwable
                                        ) {
                                            Toast.makeText(this@VerifyActivity, "Lỗi: ${t.message}", Toast.LENGTH_LONG).show()
                                        }

                                    })
                                    Toast.makeText(this@VerifyActivity,"OTP verified ${username}", Toast.LENGTH_LONG).show()
                                }else{
                                    Toast.makeText(this@VerifyActivity, "Enter correct OTP", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                }else{
                    Toast.makeText(this@VerifyActivity, "OTP not verified", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this@VerifyActivity, "Please enter all number", Toast.LENGTH_LONG).show();
            }
        }

        numberOTPmove()

        resendotp = findViewById(R.id.textResendOTP)
        resendotp.setOnClickListener{
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+84" + intent.getStringExtra("mobile"),
                60,
                TimeUnit.SECONDS,
                this@VerifyActivity,
                object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                    override fun onVerificationCompleted(p0: PhoneAuthCredential){

                    }
                    override fun onVerificationFailed(p0: FirebaseException){
                        Toast.makeText(this@VerifyActivity, p0.message, Toast.LENGTH_SHORT).show()
                    }
                    override fun onCodeSent(backendotp: String, p1: PhoneAuthProvider.ForceResendingToken){

                        getotpbackend = backendotp
                        Toast.makeText(this@VerifyActivity, "OTP sented successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }

    fun numberOTPmove(){
        input_opt1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Called before the text is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Called while the text is being changed
//                Log.d("TextWatcher", "Text changed to: $s")
                if (!s.toString().trim().isEmpty()) {
                    input_opt2.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        input_opt2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Called before the text is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Called while the text is being changed
//                Log.d("TextWatcher", "Text changed to: $s")
                if (!s.toString().trim().isEmpty()) {
                    input_opt3.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        input_opt3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Called before the text is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Called while the text is being changed
//                Log.d("TextWatcher", "Text changed to: $s")
                if (!s.toString().trim().isEmpty()) {
                    input_opt4.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        input_opt4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Called before the text is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Called while the text is being changed
//                Log.d("TextWatcher", "Text changed to: $s")
                if (!s.toString().trim().isEmpty()) {
                    input_opt5.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        input_opt5.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Called before the text is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Called while the text is being changed
//                Log.d("TextWatcher", "Text changed to: $s")
                if (!s.toString().trim().isEmpty()) {
                    input_opt6.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

    }
}