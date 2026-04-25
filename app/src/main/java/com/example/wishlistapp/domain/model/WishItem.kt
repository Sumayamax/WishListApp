package com.example.wishlistapp.domain.model

data class WishItem(
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val type: WishType,
    val category: WishCategory = WishCategory.OTHER,
    val price: Double? = null,
    val status: WishStatus = WishStatus.WISH,
    val createdAt: Long = System.currentTimeMillis()
)
