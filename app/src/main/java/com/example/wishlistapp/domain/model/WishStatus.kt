package com.example.wishlistapp.domain.model

import com.example.wishlistapp.R

enum class WishStatus(val resId: Int) {
    WISH(R.string.status_active),
    COMPLETED(R.string.status_completed)
}
