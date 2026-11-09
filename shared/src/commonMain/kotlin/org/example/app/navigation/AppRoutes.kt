package org.example.app.navigation

import kotlinx.serialization.Serializable

/**
 * Add a `data object` per destination. The compose-nav3 plugin reads
 * @Serializable types as routes, so each route becomes a strongly-typed key.
 */
sealed interface AppRoute {
    @Serializable data object Home : AppRoute
}
