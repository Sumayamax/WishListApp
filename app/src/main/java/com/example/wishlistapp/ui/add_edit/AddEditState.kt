package com.example.wishlistapp.ui.add_edit

import com.example.wishlistapp.data.remote.dto.ProductDto
import com.example.wishlistapp.domain.model.WishCategory
import com.example.wishlistapp.domain.model.WishStatus
import com.example.wishlistapp.domain.model.WishType

data class AddEditState(
    val title: String = "",
    val description: String = "",
    val type: WishType = WishType.THING,
    val category: WishCategory = WishCategory.OTHER,
    val price: String = "",
    val status: WishStatus = WishStatus.WISH,
    val imageUrl: String = "",
    val targetDate: String = "",
    val isTitleError: Boolean = false,
    val isSaved: Boolean = false,
    
    // API Search related
    val suggestions: List<ProductDto> = emptyList(),
    val isSearching: Boolean = false
)
