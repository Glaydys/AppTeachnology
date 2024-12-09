package com.example.myapplication.Retrofit

data class PaymentRequest(
    val userId: String,
    val products: List<ProductInCart>,
    val amount: Int,
    val bankCode: String = "",
    val language: String = "vn"
)

data class PaymentResponse(
    val paymentUrl: String
)
