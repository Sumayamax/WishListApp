package com.example.wishlistapp.domain.repository

import com.example.wishlistapp.data.remote.dto.ProductDto
import com.example.wishlistapp.domain.model.WishItem
import com.example.wishlistapp.domain.model.WishStatus
import com.example.wishlistapp.domain.model.WishType
import com.example.wishlistapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface WishRepository {
    fun getAllWishes(): Flow<List<WishItem>>
    suspend fun getWishById(id: Int): WishItem?
    suspend fun insertWish(wish: WishItem)
    suspend fun updateWish(wish: WishItem)
    suspend fun deleteWish(wish: WishItem)
    fun getWishesByType(type: WishType): Flow<List<WishItem>>
    fun getWishesByStatus(status: WishStatus): Flow<List<WishItem>>
    
    suspend fun searchProducts(query: String): Resource<List<ProductDto>>
}
