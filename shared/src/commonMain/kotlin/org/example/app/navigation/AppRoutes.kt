package org.example.app.navigation

import kotlinx.serialization.Serializable

sealed interface AppRoute {
    @Serializable data object Home : AppRoute
    @Serializable data object VideoList : AppRoute
    @Serializable data object VideoUpload : AppRoute
    @Serializable data class VideoPlayer(val videoId: String) : AppRoute
}
