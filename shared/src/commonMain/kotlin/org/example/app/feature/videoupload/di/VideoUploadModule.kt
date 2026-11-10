package org.example.app.feature.videoupload.di

import org.example.app.feature.videoupload.presentation.VideoUploadStore
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val videoUploadModule = module {
    factoryOf(::VideoUploadStore)
}
