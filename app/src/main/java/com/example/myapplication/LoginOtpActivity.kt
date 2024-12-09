package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class LoginOtpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_otp)

        val enterNumber: EditText = findViewById(R.id.input_phonenumber)
        val buttonGetOTP: Button = findViewById(R.id.buttonget_otp)
        val buttonLogin: TextView = findViewById(R.id.buttonLogin)

        buttonLogin.setOnClickListener{
            val intent = Intent(this@LoginOtpActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        val progressBar: ProgressBar = findViewById(R.id.progressSendOTP)

        buttonGetOTP.setOnClickListener{
            if(!enterNumber.text.toString().trim().isEmpty()){
                if((enterNumber.text.toString().trim()).length == 10){

                    progressBar.visibility = View.VISIBLE
                    buttonGetOTP.visibility = View.INVISIBLE

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+84" + enterNumber.text.toString().trim(),
                        60,
                        TimeUnit.SECONDS,
                        this@LoginOtpActivity,
                        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                            override fun onVerificationCompleted(p0: PhoneAuthCredential){
                                progressBar.visibility = View.VISIBLE
                                buttonGetOTP.visibility = View.INVISIBLE
                            }
                            override fun onVerificationFailed(p0: FirebaseException){
                                progressBar.visibility = View.VISIBLE
                                buttonGetOTP.visibility = View.INVISIBLE
                                p0.printStackTrace() // In lỗi vào logcat
                                Toast.makeText(this@LoginOtpActivity, p0.localizedMessage ?: "Verification failed", Toast.LENGTH_SHORT).show()
                            }
                            override fun onCodeSent(backendotp: String, p1: PhoneAuthProvider.ForceResendingToken){
                                progressBar.visibility = View.VISIBLE
                                buttonGetOTP.visibility = View.INVISIBLE

                                val intent = Intent(getApplicationContext(), VerifyActivity::class.java)
                                intent.putExtra("mobile", enterNumber.getText().toString());
                                intent.putExtra("backendotp", backendotp);
                                startActivity(intent);

                                progressBar.visibility = View.INVISIBLE
                            }
                        }
                    )
                }else{
                    Toast.makeText(this@LoginOtpActivity, "Please enter correct number", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this@LoginOtpActivity, "Please enter a mobile number", Toast.LENGTH_SHORT).show();
            }
        }
    }
}