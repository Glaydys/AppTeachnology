package com.example.myapplication.Retrofit


import retrofit2.Call
import retrofit2.http.Body
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

    @POST("rate/rate")
    fun addRate(@Body rate: Rate): Call<Rate>

    @GET("rate/rate/{productId}")
    fun getComment(@Path("productId") productId: String): Call<RatingResponse>

    @POST("cart/addproduct_cart")
    fun addtoCart(@Body cart: Cart): Call<Cart>

    @GET("cart/{userId}")
    fun getCart(@Path("userId") userId: String): Call<CartResponse>

    @POST("cart/update")
    fun updateCart(@Body cartUpdateRequest: CartUpdateRequest): Call<CartResponse>

    @POST("cart/delete_product_cart")
    fun deleteProductFromCart(@Body cartDelete: CartDelete): Call<CartResponse>

    @POST("vnpay/create_payment_url")
    fun createPaymentUrl(@Body request: PaymentRequest): Call<PaymentResponse>
}

