package com.example.wishlistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wishlistapp.ui.add_edit.AddEditScreen
import com.example.wishlistapp.ui.detail.DetailScreen
import com.example.wishlistapp.ui.home.HomeScreen
import com.example.wishlistapp.ui.navigation.Screen
import com.example.wishlistapp.ui.stats.StatsScreen
import com.example.wishlistapp.ui.theme.WishListAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WishListAppTheme {
                WishNavHost()
            }
        }
    }
}

@Composable
fun WishNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onAddWish = {
                    navController.navigate(Screen.AddEdit.passWishId())
                },
                onWishClick = { wishId ->
                    navController.navigate(Screen.Detail.passWishId(wishId))
                },
                onStatsClick = {
                    navController.navigate(Screen.Stats.route)
                }
            )
        }
        composable(Screen.Stats.route) {
            StatsScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("wishId") {
                    type = NavType.IntType
                }
            )
        ) {
            DetailScreen(
                onBack = {
                    navController.popBackStack()
                },
                onEdit = { wishId ->
                    navController.navigate(Screen.AddEdit.passWishId(wishId))
                }
            )
        }
        composable(
            route = Screen.AddEdit.route,
            arguments = listOf(
                navArgument("wishId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) {
            AddEditScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
