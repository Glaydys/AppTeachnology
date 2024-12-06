package com.example.myapplication.Retrofit

data class OrderDetail(
    val _id: String,
    val productId: Product,
    val quantity: Int,
    val date: String
)