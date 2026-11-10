package org.example.app

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.example.app.feature.videolist.ui.VideoListScreen
import org.example.app.feature.videoupload.ui.VideoUploadScreen
import org.example.app.navigation.AppRoute

@Composable
fun App() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = AppRoute.VideoList
    ) {
        composable<AppRoute.VideoList> {
            VideoListScreen(
                onVideoClick = { videoId ->
                    navController.navigate(AppRoute.VideoPlayer(videoId))
                },
                onUploadClick = {
                    navController.navigate(AppRoute.VideoUpload)
                }
            )
        }
        
        composable<AppRoute.VideoUpload> {
            VideoUploadScreen(
                onUploadComplete = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable<AppRoute.VideoPlayer> { backStackEntry ->
            val route = backStackEntry.toRoute<AppRoute.VideoPlayer>()
            // بعد میں VideoPlayerScreen بنائیں گے
        }
    }
}
