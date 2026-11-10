package org.example.app.feature.videoplayer.di

import org.example.app.feature.videoplayer.presentation.VideoPlayerStore
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val videoPlayerModule = module {
    factoryOf(::VideoPlayerStore)
}
