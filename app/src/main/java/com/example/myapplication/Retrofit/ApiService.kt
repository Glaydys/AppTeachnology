package com.example.myapplication.Retrofit


import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface ApiService {
    @POST("user/login")
    fun login(@Body user: User): Call<LoginResponse>

    @POST("user/register")
    fun register(@Body user: User): Call<RegisterResponse>

    @GET("categories")
    fun getCategories(): Call<List<category>>

    @GET("products")
    fun getProducts(): Call<List<products>>
}

