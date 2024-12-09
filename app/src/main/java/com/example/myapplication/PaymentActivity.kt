package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class PaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val paymentWebView: WebView = findViewById(R.id.paymentWebView)
        val paymentUrl = intent.getStringExtra("PAYMENT_URL")

        paymentWebView.settings.javaScriptEnabled = true
        paymentWebView.webViewClient = WebViewClient()

        paymentWebView.addJavascriptInterface(object {
            @JavascriptInterface
            fun paymentSuccess() {
                val intent = Intent(this@PaymentActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, "AndroidInterface")
        if (paymentUrl != null) {
            paymentWebView.loadUrl(paymentUrl)
        }
    }
}
