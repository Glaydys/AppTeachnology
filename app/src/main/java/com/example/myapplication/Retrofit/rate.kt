package com.example.myapplication.Retrofit

data class Rate(
    val userId: String,
    val productId: String,
    val comment: String,
    val rating: Float
)

data class RatingResponse(
    val averageRating: String,
    val totalComments: Int,
    val users: List<User_comment>
)

data class User_comment(
    val userId: UserId,
    val comment: String,
    val rate: Int,
    val createdAt: String,
    val _id: String
)

data class UserId(
    val _id: String,
    val username: String
)