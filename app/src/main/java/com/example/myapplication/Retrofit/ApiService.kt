package com.example.myapplication.Retrofit


import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface ApiService {
    @POST("user/login")
    fun login(@Body user: User): Call<LoginResponse>

    @POST("user/register")
    fun register(@Body user: User): Call<RegisterResponse>

    @GET("categories")
    fun getCategories(): Call<List<category>>

    @GET("products")
    fun getProducts(): Call<List<products>>

    @POST("rate/add")
    fun addRate(@Body rate: Rate): Call<Rate>

    @POST("carts/add")
    fun addtoCart(@Body cart: Cart): Call<Cart>

    @GET("carts/{userId}")
    fun getCart(@Path("userId") userId: String): Call<CartResponse>

    @POST("carts/update")
    fun updateCart(@Body cartUpdateRequest: CartUpdateRequest): Call<CartResponse>

    @DELETE("carts/{productId}/{userId}")
    fun deleteProductFromCart(
        @Path("productId") productId: String,
        @Path("userId") userId: String
    ): Call<CartResponse>

}

