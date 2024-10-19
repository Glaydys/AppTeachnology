package com.example.myapplication


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface MyApi {
    @GET("categories")
    fun getCategories(): Call<List<category>>

    @GET("products")
    fun getProducts(): Call<List<products>>
}

