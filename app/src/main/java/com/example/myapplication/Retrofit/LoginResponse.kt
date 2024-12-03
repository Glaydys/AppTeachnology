package com.example.myapplication.Retrofit

data class LoginResponse(
    val user: UserResponse,
    val token: String
)

data class UserResponse(
    val _id: String,
    val username: String,
    val email: String,
    val address: String?,
    val createdAt: String,
    val updatedAt: String
)