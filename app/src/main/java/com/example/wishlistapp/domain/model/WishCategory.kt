package com.example.wishlistapp.domain.model

import com.example.wishlistapp.R

enum class WishCategory(val resId: Int) {
    TECH(R.string.category_tech),
    CLOTHES(R.string.category_clothes),
    TRAVEL(R.string.category_travel),
    GROWTH(R.string.category_growth),
    OTHER(R.string.category_other)
}
