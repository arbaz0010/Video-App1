package org.example.app.feature.videolist.di

import org.example.app.feature.videolist.presentation.VideoListStore
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val videoListModule = module {
    factoryOf(::VideoListStore)
}
