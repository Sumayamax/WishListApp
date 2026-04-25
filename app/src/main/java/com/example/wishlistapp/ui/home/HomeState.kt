package com.example.wishlistapp.ui.home

import com.example.wishlistapp.domain.model.WishItem
import com.example.wishlistapp.data.local.preferences.UserPreferences

data class HomeState(
    val wishes: List<WishItem> = emptyList(),
    val userPreferences: UserPreferences = UserPreferences("ALL", "ALL", "DATE", 0.0),
    val isLoading: Boolean = false,
    val totalWishes: Int = 0,
    val completedWishes: Int = 0,
    val progressPercentage: Float = 0f,
    // Budget related
    val usedBudget: Double = 0.0,
    val remainingBudget: Double = 0.0,
    val isOverBudget: Boolean = false
)
