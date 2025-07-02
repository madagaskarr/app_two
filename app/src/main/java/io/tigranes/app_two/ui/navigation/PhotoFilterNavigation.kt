package io.tigranes.app_two.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.tigranes.app_two.ui.screens.HomeScreen

@Composable
fun PhotoFilterNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = PhotoFilterScreen.Home.route
    ) {
        composable(route = PhotoFilterScreen.Home.route) {
            HomeScreen()
        }
        
        composable(route = PhotoFilterScreen.Camera.route) {
            // CameraScreen will be implemented later
        }
        
        composable(route = PhotoFilterScreen.Editor.route) {
            // EditorScreen will be implemented later
        }
    }
}

sealed class PhotoFilterScreen(val route: String) {
    object Home : PhotoFilterScreen("home")
    object Camera : PhotoFilterScreen("camera")
    object Editor : PhotoFilterScreen("editor")
}