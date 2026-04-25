package com.example.wishlistapp.ui.detail

import com.example.wishlistapp.domain.model.WishItem

data class DetailState(
    val wish: WishItem? = null,
    val isLoading: Boolean = false
)
