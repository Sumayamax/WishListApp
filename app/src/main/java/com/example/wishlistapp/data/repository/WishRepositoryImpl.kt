package com.example.wishlistapp.data.repository

import com.example.wishlistapp.data.local.dao.WishDao
import com.example.wishlistapp.data.local.entity.WishEntity
import com.example.wishlistapp.data.remote.DummyJsonApi
import com.example.wishlistapp.data.remote.dto.ProductDto
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
        dao.insertWish(WishEntity.fromWishItem(wish))
    }

    override suspend fun updateWish(wish: WishItem) {
        dao.updateWish(WishEntity.fromWishItem(wish))
    }

    override suspend fun deleteWish(wish: WishItem) {
        dao.deleteWish(WishEntity.fromWishItem(wish))
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

    override suspend fun searchProducts(query: String): Resource<List<ProductDto>> {
        return try {
            val response = api.searchProducts(query)
            Resource.Success(response.products)
        } catch (e: IOException) {
            Resource.Error("Couldn't reach server. Check your internet connection.")
        } catch (e: HttpException) {
            Resource.Error("Server error: ${e.code()}. Please try again later.")
        } catch (e: Exception) {
            Resource.Error("An unexpected error occurred: ${e.localizedMessage}")
        }
    }
}
