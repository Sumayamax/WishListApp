package com.example.wishlistapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Database entity for Room. 
 * Kept as a pure data holder to follow Clean Architecture principles.
 */
@Entity(tableName = "wishes")
data class WishEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val type: String,
    val category: String = "OTHER",
    val price: Double?,
    val status: String,
    val imageUrl: String = "",
    val targetDate: String = "",
    val createdAt: Long
)
