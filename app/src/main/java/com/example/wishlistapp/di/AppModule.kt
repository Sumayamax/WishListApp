package com.example.wishlistapp.di

import android.content.Context
import androidx.room.Room
import com.example.wishlistapp.data.local.WishDatabase
import com.example.wishlistapp.data.local.dao.WishDao
import com.example.wishlistapp.data.local.preferences.PreferenceManager
import com.example.wishlistapp.data.repository.SettingsRepositoryImpl
import com.example.wishlistapp.data.repository.WishRepositoryImpl
import com.example.wishlistapp.domain.repository.SettingsRepository
import com.example.wishlistapp.domain.repository.WishRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
            WishDatabase.DATABASE_NAME
        )
        .addMigrations(WishDatabase.MIGRATION_1_2)
        .build()
    }

    @Provides
    @Singleton
    fun provideWishDao(db: WishDatabase): WishDao {
        return db.wishDao
    }

    @Provides
    @Singleton
    fun provideWishRepository(dao: WishDao): WishRepository {
        return WishRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(preferenceManager: PreferenceManager): SettingsRepository {
        return SettingsRepositoryImpl(preferenceManager)
    }
}
