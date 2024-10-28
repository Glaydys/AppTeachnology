package com.example.myapplication.Retrofit

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val username: String,
    val email: String
)
