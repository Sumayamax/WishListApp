package com.example.wishlistapp.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class PreferenceManager @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.dataStore

    private object PreferencesKeys {
        val FILTER_TYPE = stringPreferencesKey("filter_type")
        val FILTER_STATUS = stringPreferencesKey("filter_status")
        val SORT_BY = stringPreferencesKey("sort_by")
        val TOTAL_BUDGET = doublePreferencesKey("total_budget")
    }

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserPreferences(
                filterType = preferences[PreferencesKeys.FILTER_TYPE] ?: "ALL",
                filterStatus = preferences[PreferencesKeys.FILTER_STATUS] ?: "ALL",
                sortBy = preferences[PreferencesKeys.SORT_BY] ?: "DATE",
                totalBudget = preferences[PreferencesKeys.TOTAL_BUDGET] ?: 0.0
            )
        }

    suspend fun updateTypeFilter(type: String) {
        dataStore.edit { preferences -> preferences[PreferencesKeys.FILTER_TYPE] = type }
    }

    suspend fun updateStatusFilter(status: String) {
        dataStore.edit { preferences -> preferences[PreferencesKeys.FILTER_STATUS] = status }
    }

    suspend fun updateSortBy(sortBy: String) {
        dataStore.edit { preferences -> preferences[PreferencesKeys.SORT_BY] = sortBy }
    }

    suspend fun updateTotalBudget(budget: Double) {
        dataStore.edit { preferences -> preferences[PreferencesKeys.TOTAL_BUDGET] = budget }
    }
}

data class UserPreferences(
    val filterType: String,
    val filterStatus: String,
    val sortBy: String,
    val totalBudget: Double
)
