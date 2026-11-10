package org.example.app.core.di

import org.example.app.feature.video.di.videoModule
import org.example.app.feature.videolist.di.videoListModule
import org.koin.core.context.startKoin
import org.koin.dsl.module

val coreModule = module {
}

fun initKoin() {
    startKoin {
        modules(
            coreModule,
            videoModule,
            videoListModule,
            homeModule,
        )
    }
}
