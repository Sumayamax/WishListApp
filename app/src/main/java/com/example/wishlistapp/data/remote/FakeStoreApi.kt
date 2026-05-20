package com.example.wishlistapp.data.remote

import com.example.wishlistapp.data.remote.dto.ProductResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FakeStoreApi {
    
    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") query: String
    ): ProductResponse

    companion object {
        // Switching to DummyJSON for much better search relevance and aesthetic thumbnails
        const val BASE_URL = "https://dummyjson.com/"
    }
}
