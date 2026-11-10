package org.example.app.core.di

import org.example.app.feature.video.di.videoModule
import org.example.app.feature.videolist.di.videoListModule
import org.example.app.feature.videoupload.di.videoUploadModule
import org.example.app.feature.videoplayer.di.videoPlayerModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(
            coreModule,          // یہ پہلے سے CoreModule.kt میں موجود ہے
            videoModule,
            videoListModule,
            videoUploadModule,   // چیک کریں کہ 'U' بڑا ہے (camelCase)
            videoPlayerModule
        )
    }
}
