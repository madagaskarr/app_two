package io.tigranes.app_two.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import io.tigranes.app_two.ui.screens.CameraScreen
import io.tigranes.app_two.ui.screens.EditorScreen
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
            HomeScreen(navController = navController)
        }
        
        composable(route = PhotoFilterScreen.Camera.route) {
            CameraScreen(navController = navController)
        }
        
        composable(
            route = PhotoFilterScreen.Editor.route + "?imageUri={imageUri}",
            arguments = listOf(
                navArgument("imageUri") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val imageUriString = backStackEntry.arguments?.getString("imageUri")
            val imageUri = imageUriString?.let { android.net.Uri.parse(it) }
            EditorScreen(
                navController = navController,
                imageUri = imageUri
            )
        }
    }
}

sealed class PhotoFilterScreen(val route: String) {
    object Home : PhotoFilterScreen("home")
    object Camera : PhotoFilterScreen("camera")
    object Editor : PhotoFilterScreen("editor")
}