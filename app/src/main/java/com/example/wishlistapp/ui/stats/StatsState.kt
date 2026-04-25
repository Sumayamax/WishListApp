package com.example.wishlistapp.ui.stats

data class StatsState(
    val totalWishes: Int = 0,
    val completedWishes: Int = 0,
    val completionRate: Float = 0f,
    val totalCost: Double = 0.0,
    val completedCost: Double = 0.0,
    val thingsCount: Int = 0,
    val experiencesCount: Int = 0,
    val isLoading: Boolean = false
)
