package com.example.wishlistapp.data.repository

import com.example.wishlistapp.data.local.dao.WishDao
import com.example.wishlistapp.data.mapper.toEntity
import com.example.wishlistapp.data.mapper.toProduct
import com.example.wishlistapp.data.mapper.toWishItem
import com.example.wishlistapp.data.remote.DummyJsonApi
import com.example.wishlistapp.domain.model.Product
import com.example.wishlistapp.domain.model.WishItem
import com.example.wishlistapp.domain.model.WishStatus
import com.example.wishlistapp.domain.model.WishType
import com.example.wishlistapp.domain.repository.WishRepository
import com.example.wishlistapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Implementation of the WishRepository.
 * Uses Clean Architecture Mappers to convert between Database Entities and Domain Models.
 */
class WishRepositoryImpl @Inject constructor(
    private val dao: WishDao,
    private val api: DummyJsonApi
) : WishRepository {

    override fun getAllWishes(): Flow<List<WishItem>> {
        return dao.getAllWishes().map { entities ->
            entities.map { it.toWishItem() }
        }
    }

    override suspend fun getWishById(id: Int): WishItem? {
        return dao.getWishById(id)?.toWishItem()
    }

    override suspend fun insertWish(wish: WishItem) {
        dao.insertWish(wish.toEntity())
    }

    override suspend fun updateWish(wish: WishItem) {
        dao.updateWish(wish.toEntity())
    }

    override suspend fun deleteWish(wish: WishItem) {
        dao.deleteWish(wish.toEntity())
    }

    override fun getWishesByType(type: WishType): Flow<List<WishItem>> {
        return dao.getWishesByType(type.name).map { entities ->
            entities.map { it.toWishItem() }
        }
    }

    override fun getWishesByStatus(status: WishStatus): Flow<List<WishItem>> {
        return dao.getWishesByStatus(status.name).map { entities ->
            entities.map { it.toWishItem() }
        }
    }

    override suspend fun searchProducts(query: String): Resource<List<Product>> {
        return try {
            val response = api.searchProducts(query)
            // Senior Level: Map DTO list to Domain model list immediately
            Resource.Success(response.products.map { it.toProduct() })
        } catch (e: IOException) {
            Resource.Error("Couldn't reach server. Check your internet connection.")
        } catch (e: HttpException) {
            Resource.Error("Server error: ${e.code()}. Please try again later.")
        } catch (e: Exception) {
            Resource.Error("An unexpected error occurred: ${e.localizedMessage ?: "Unknown error"}")
        }
    }
}
