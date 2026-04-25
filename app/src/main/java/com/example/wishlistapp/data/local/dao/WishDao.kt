package com.example.wishlistapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.wishlistapp.data.local.entity.WishEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WishDao {
    @Query("SELECT * FROM wishes ORDER BY createdAt DESC")
    fun getAllWishes(): Flow<List<WishEntity>>

    @Query("SELECT * FROM wishes WHERE id = :id")
    suspend fun getWishById(id: Int): WishEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWish(wish: WishEntity)

    @Update
    suspend fun updateWish(wish: WishEntity)

    @Delete
    suspend fun deleteWish(wish: WishEntity)

    @Query("SELECT * FROM wishes WHERE type = :type")
    fun getWishesByType(type: String): Flow<List<WishEntity>>

    @Query("SELECT * FROM wishes WHERE status = :status")
    fun getWishesByStatus(status: String): Flow<List<WishEntity>>
}
