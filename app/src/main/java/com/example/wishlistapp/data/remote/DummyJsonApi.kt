package com.example.wishlistapp.data.remote

import com.example.wishlistapp.data.remote.dto.ProductResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Modern API interface for DummyJSON.
 * Standardizes the endpoint for product searching.
 */
interface DummyJsonApi {
    
    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") query: String
    ): ProductResponse
}
