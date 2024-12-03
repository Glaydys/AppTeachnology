package com.example.myapplication.Retrofit

data class CartResponse(
    val _id: String,
    val products: List<ProductInCart>,
    val __v: Int
)

data class ProductInCart(
    val productId: Product,
    var quantity: Int,
    var isChecked: Boolean = false
)

data class Product(
    val _id: String,
    val category_id: Int,
    val name_product: String,
    val description: String,
    val price: Int,
    val quantity: Int,
    val image_product: String,
    val __v: Int
)


data class Cart(
    val userId: String,
    val productId: String,
    val quantity: Int,
    )


data class CartUpdateRequest(
    val userId: String,
    val products: List<Cart>
)

data class CartDelete(
    val userId: String,
    val productId: String
)

