package com.example.wishlistapp.di

import android.content.Context
import androidx.room.Room
import com.example.wishlistapp.data.local.WishDatabase
import com.example.wishlistapp.data.local.dao.WishDao
import com.example.wishlistapp.data.local.preferences.PreferenceManager
import com.example.wishlistapp.data.remote.DummyJsonApi
import com.example.wishlistapp.data.repository.SettingsRepositoryImpl
import com.example.wishlistapp.data.repository.WishRepositoryImpl
import com.example.wishlistapp.domain.repository.SettingsRepository
import com.example.wishlistapp.domain.repository.WishRepository
import com.example.wishlistapp.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWishDatabase(@ApplicationContext context: Context): WishDatabase {
        return Room.databaseBuilder(
            context,
            WishDatabase::class.java,
            Constants.DATABASE_NAME
        )
        .addMigrations(
            WishDatabase.MIGRATION_1_2,
            WishDatabase.MIGRATION_2_3,
            WishDatabase.MIGRATION_3_4
        )
        .build()
    }

    @Provides
    @Singleton
    fun provideWishDao(db: WishDatabase): WishDao {
        return db.wishDao
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideDummyJsonApi(okHttpClient: OkHttpClient): DummyJsonApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DummyJsonApi::class.java)
    }

    @Provides
    @Singleton
    fun provideWishRepository(
        dao: WishDao,
        api: DummyJsonApi
    ): WishRepository {
        return WishRepositoryImpl(dao, api)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(preferenceManager: PreferenceManager): SettingsRepository {
        return SettingsRepositoryImpl(preferenceManager)
    }
}
