package com.example.wishlistapp.data.repository

import com.example.wishlistapp.data.local.preferences.PreferenceManager
import com.example.wishlistapp.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val preferenceManager: PreferenceManager
) : SettingsRepository {

    override val isDarkTheme: Flow<Boolean> = preferenceManager.userPreferencesFlow.map { 
        it.isDarkTheme 
    }

    override suspend fun setDarkTheme(isDark: Boolean) {
        preferenceManager.updateDarkTheme(isDark)
    }
}
