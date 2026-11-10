package org.example.app.feature.video.di

import org.example.app.feature.video.data.LocalVideoRepository
import org.example.app.feature.video.data.VideoRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val videoModule = module {
    singleOf(::LocalVideoRepository) bind VideoRepository::class
}
