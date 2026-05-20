package com.example.wishlistapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    val products: List<ProductDto>
)

data class ProductDto(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    @SerializedName("thumbnail")
    val image: String
)
