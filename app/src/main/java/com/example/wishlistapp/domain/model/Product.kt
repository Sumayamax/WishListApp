package com.example.wishlistapp.domain.model

/**
 * Clean Domain model for Product.
 * Domain layer doesn't know about Retrofit or SerializedName.
 */
data class Product(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val imageUrl: String
)
