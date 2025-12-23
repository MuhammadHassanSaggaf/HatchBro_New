package com.hatchbro.app.ui.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object IncubatorList : Screen("incubators")
    object IncubatorDetail : Screen("incubator/{incubatorId}") {
        fun createRoute(incubatorId: Long) = "incubator/$incubatorId"
    }
    object BatchWizard : Screen("batch/new")
    object BatchDetail : Screen("batch/{batchId}") {
        fun createRoute(batchId: Long) = "batch/$batchId"
    }
    object Catalog : Screen("catalog")
    object Settings : Screen("settings")
}
