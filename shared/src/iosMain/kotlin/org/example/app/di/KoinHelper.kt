package org.example.app.di

import org.example.app.core.di.initKoin
import org.example.app.feature.home.presentation.HomeStore
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 * Bridge from Swift → Kotlin DI graph. SKiE generates Swift-friendly accessors
 * for everything exposed here.
 */
object KoinHelper : KoinComponent {
    val homeStore: HomeStore get() = get()
    fun doInitKoin() = initKoin()
}
