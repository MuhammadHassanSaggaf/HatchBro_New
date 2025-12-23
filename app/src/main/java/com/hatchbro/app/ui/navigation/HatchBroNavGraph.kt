package com.hatchbro.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hatchbro.app.ui.screens.batch.BatchDetailScreen
import com.hatchbro.app.ui.screens.batch.BatchWizardScreen
import com.hatchbro.app.ui.screens.dashboard.DashboardScreen
import com.hatchbro.app.ui.screens.incubator.IncubatorDetailScreen
import com.hatchbro.app.ui.screens.incubator.IncubatorListScreen
import com.hatchbro.app.ui.screens.settings.SettingsScreen

@Composable
fun HatchBroNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Dashboard.route) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController = navController)
        }
        composable(Screen.IncubatorList.route) {
            IncubatorListScreen(navController = navController)
        }
        composable(
            route = Screen.IncubatorDetail.route,
            arguments = listOf(navArgument("incubatorId") { type = NavType.LongType })
        ) { backStackEntry ->
            val incubatorId = backStackEntry.arguments?.getLong("incubatorId") ?: 0L
            IncubatorDetailScreen(navController = navController, incubatorId = incubatorId)
        }
        composable(Screen.BatchWizard.route) {
            BatchWizardScreen(navController = navController)
        }
        composable(
            route = Screen.BatchDetail.route,
            arguments = listOf(navArgument("batchId") { type = NavType.LongType })
        ) { backStackEntry ->
            val batchId = backStackEntry.arguments?.getLong("batchId") ?: 0L
            BatchDetailScreen(navController = navController, batchId = batchId)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
        composable(Screen.Catalog.route) {
            com.hatchbro.app.ui.screens.catalog.CatalogScreen(navController = navController)
        }
    }
}
