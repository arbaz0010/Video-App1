package org.example.app.ios

import org.example.app.feature.videolist.presentation.VideoListStore
import org.example.app.feature.videoupload.presentation.VideoUploadStore
import org.example.app.feature.videoplayer.presentation.VideoPlayerStore
import org.koin.mp.KoinPlatform

object KoinHelper {
    val shared = KoinHelper
    
    val videoListStore: VideoListStore 
        get() = KoinPlatform.getKoin().get()
    
    val videoUploadStore: VideoUploadStore 
        get() = KoinPlatform.getKoin().get()
    
    val videoPlayerStore: VideoPlayerStore 
        get() = KoinPlatform.getKoin().get()
}
