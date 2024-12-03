package com.example.myapplication.Retrofit

data class PaymentRequest(
    val userId: String,
    val products: List<ProductInCart>,
    val amount: Int,
    val bankCode: String = "",  // Default value is an empty string
    val language: String = "vn"  // Default value is "vn"
)

data class PaymentResponse(
    val paymentUrl: String
)
