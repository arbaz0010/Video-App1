package org.example.app.navigation

import kotlinx.serialization.Serializable

sealed interface AppRoute {
    @Serializable data object Home : AppRoute
    @Serializable data object VideoList : AppRoute  // ← یہ شامل کریں
    @Serializable data class VideoPlayer(val videoId: String) : AppRoute  // ← یہ بھی شامل کریں
}
