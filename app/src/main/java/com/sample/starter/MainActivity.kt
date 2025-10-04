package com.sample.starter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.sample.feed.ui.theme.StarterTheme
import com.sample.starter.ui.list.UserListScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sample.starter.ui.details.UserDetailsScreen

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StarterTheme {
                AppNavigation()
            }
        }
    }

    @Composable
    fun AppNavigation() {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = UserListRoute
        ) {
            composable<UserListRoute> {
                UserListScreen(
                    onUserClick = { userId -> navController.navigate(UserDetailRoute(userId = userId)) }
                )
            }

            composable<UserDetailRoute> { backStackEntry ->
                UserDetailsScreen {
                    navController.navigateUp()
                }
            }
        }

    }


    @Serializable
    data object UserListRoute

    @Serializable
    data class UserDetailRoute(val userId: Int)
}