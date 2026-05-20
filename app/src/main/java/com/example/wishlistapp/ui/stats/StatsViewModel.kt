package com.example.wishlistapp.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wishlistapp.domain.model.WishStatus
import com.example.wishlistapp.domain.model.WishType
import com.example.wishlistapp.domain.repository.WishRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val repository: WishRepository
) : ViewModel() {

    val state: StateFlow<StatsState> = repository.getAllWishes()
        .catch { emit(emptyList()) }
        .map { wishes ->
            val total = wishes.size
            val completed = wishes.count { it.status == WishStatus.COMPLETED }
            val things = wishes.filter { it.type == WishType.THING }
            val experiencesCount = wishes.count { it.type == WishType.EXPERIENCE }
            
            val totalCost = things.sumOf { it.price ?: 0.0 }
            val completedCost = things.filter { it.status == WishStatus.COMPLETED }.sumOf { it.price ?: 0.0 }

            StatsState(
                totalWishes = total,
                completedWishes = completed,
                completionRate = if (total > 0) completed.toFloat() / total else 0f,
                totalCost = totalCost,
                completedCost = completedCost,
                thingsCount = things.size,
                experiencesCount = experiencesCount,
                isLoading = false
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = StatsState(isLoading = true)
        )
}
