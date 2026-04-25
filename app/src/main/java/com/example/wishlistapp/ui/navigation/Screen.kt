package com.example.wishlistapp.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Stats : Screen("stats")
    object AddEdit : Screen("add_edit?wishId={wishId}") {
        fun passWishId(wishId: Int? = null): String {
            return "add_edit?wishId=${wishId ?: -1}"
        }
    }
    object Detail : Screen("detail/{wishId}") {
        fun passWishId(wishId: Int): String {
            return "detail/$wishId"
        }
    }
}
