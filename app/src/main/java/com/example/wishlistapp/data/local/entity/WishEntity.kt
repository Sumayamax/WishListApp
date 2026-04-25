package com.example.wishlistapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.wishlistapp.domain.model.WishCategory
import com.example.wishlistapp.domain.model.WishItem
import com.example.wishlistapp.domain.model.WishStatus
import com.example.wishlistapp.domain.model.WishType

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
    val createdAt: Long
) {
    fun toWishItem(): WishItem = WishItem(
        id = id,
        title = title,
        description = description,
        type = WishType.valueOf(type),
        category = try { WishCategory.valueOf(category) } catch (e: Exception) { WishCategory.OTHER },
        price = price,
        status = WishStatus.valueOf(status),
        createdAt = createdAt
    )

    companion object {
        fun fromWishItem(item: WishItem): WishEntity = WishEntity(
            id = item.id,
            title = item.title,
            description = item.description,
            type = item.type.name,
            category = item.category.name,
            price = item.price,
            status = item.status.name,
            createdAt = item.createdAt
        )
    }
}
