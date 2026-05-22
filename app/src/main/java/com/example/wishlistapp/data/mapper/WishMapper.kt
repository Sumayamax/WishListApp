package com.example.wishlistapp.data.mapper

import com.example.wishlistapp.data.local.entity.WishEntity
import com.example.wishlistapp.data.remote.dto.ProductDto
import com.example.wishlistapp.domain.model.*

/**
 * Mappers are essential for Clean Architecture to keep the Data layer logic
 * separate from Domain models.
 */

fun WishEntity.toWishItem(): WishItem = WishItem(
    id = id,
    title = title,
    description = description,
    type = try { WishType.valueOf(type) } catch (e: Exception) { WishType.THING },
    category = try { WishCategory.valueOf(category) } catch (e: Exception) { WishCategory.OTHER },
    price = price,
    status = try { WishStatus.valueOf(status) } catch (e: Exception) { WishStatus.WISH },
    imageUrl = imageUrl,
    targetDate = targetDate,
    createdAt = createdAt
)

fun WishItem.toEntity(): WishEntity = WishEntity(
    id = id,
    title = title,
    description = description,
    type = type.name,
    category = category.name,
    price = price,
    status = status.name,
    imageUrl = imageUrl,
    targetDate = targetDate,
    createdAt = createdAt
)

fun ProductDto.toProduct(): Product = Product(
    id = id,
    title = title,
    price = price,
    description = description,
    imageUrl = image
)
