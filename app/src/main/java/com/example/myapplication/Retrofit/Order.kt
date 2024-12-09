package com.example.myapplication.Retrofit

data class Order(
    val _id: String,
    val userId: String,
    val OrderDate: String,
    val TotalAmount: Int,
    val OrderCode: String,
    val Status: String,
)

