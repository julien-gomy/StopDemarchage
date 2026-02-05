package com.stopdemarchage.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.stopdemarchage.ui.screens.HistoryScreen
import com.stopdemarchage.ui.screens.HomeScreen
import com.stopdemarchage.ui.screens.PrefixListScreen
import com.stopdemarchage.ui.screens.SettingsScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Prefixes : Screen("prefixes")
    object History : Screen("history")
    object Settings : Screen("settings")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    onRequestScreeningRole: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToPrefixes = { navController.navigate(Screen.Prefixes.route) },
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onRequestScreeningRole = onRequestScreeningRole
            )
        }
        composable(Screen.Prefixes.route) {
            PrefixListScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
