package org.example.app.core.di

import org.example.app.feature.home.di.homeModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(
            coreModule,
            homeModule,
        )
    }
}
