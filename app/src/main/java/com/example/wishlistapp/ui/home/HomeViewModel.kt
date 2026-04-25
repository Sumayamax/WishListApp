package com.example.wishlistapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wishlistapp.data.local.preferences.PreferenceManager
import com.example.wishlistapp.domain.model.WishItem
import com.example.wishlistapp.domain.model.WishStatus
import com.example.wishlistapp.domain.model.WishType
import com.example.wishlistapp.domain.repository.WishRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: WishRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    val state: StateFlow<HomeState> = combine(
        repository.getAllWishes(),
        preferenceManager.userPreferencesFlow
    ) { wishes, preferences ->
        // Only show active wishes on Home screen
        val activeWishes = wishes.filter { it.status == WishStatus.WISH }
        
        val filteredWishes = activeWishes.filter { wish ->
            preferences.filterType == "ALL" || wish.type.name == preferences.filterType
        }.let { list ->
            when (preferences.sortBy) {
                "PRICE_ASC" -> list.sortedBy { it.price ?: 0.0 }
                "PRICE_DESC" -> list.sortedByDescending { it.price ?: 0.0 }
                else -> list.sortedByDescending { it.createdAt }
            }
        }

        val total = wishes.size
        val completed = wishes.count { it.status == WishStatus.COMPLETED }
        val progress = if (total > 0) completed.toFloat() / total else 0f

        // Budget Calculations
        val usedBudget = wishes.filter { it.type == WishType.THING }.sumOf { it.price ?: 0.0 }
        val remainingBudget = preferences.totalBudget - usedBudget
        val isOverBudget = usedBudget > preferences.totalBudget && preferences.totalBudget > 0

        HomeState(
            wishes = filteredWishes,
            userPreferences = preferences,
            totalWishes = total,
            completedWishes = completed,
            progressPercentage = progress,
            usedBudget = usedBudget,
            remainingBudget = remainingBudget,
            isOverBudget = isOverBudget
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeState())

    fun onToggleStatus(wish: WishItem) {
        viewModelScope.launch {
            val newStatus = if (wish.status == WishStatus.WISH) WishStatus.COMPLETED else WishStatus.WISH
            repository.updateWish(wish.copy(status = newStatus))
        }
    }

    fun onDeleteWish(wish: WishItem) {
        viewModelScope.launch {
            repository.deleteWish(wish)
        }
    }

    fun onTypeFilterChanged(type: String) {
        viewModelScope.launch {
            preferenceManager.updateTypeFilter(type)
        }
    }

    fun onUpdateBudget(budget: Double) {
        viewModelScope.launch {
            preferenceManager.updateTotalBudget(budget)
        }
    }
}
