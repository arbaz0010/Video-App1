package org.example.app.feature.videoplayer.presentation

import org.example.app.core.mvi.MviStore
import org.example.app.feature.video.data.VideoRepository

data class VideoPlayerState(
    val videoId: String = "",
    val videoTitle: String = "",
    val videoUrl: String = "",
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentTime: Long = 0, // سیکنڈز میں
    val duration: Long = 0, // سیکنڈز میں
    val error: String? = null
) {
    val progress: Float
        get() = if (duration > 0) currentTime.toFloat() / duration.toFloat() else 0f
    
    val formattedCurrentTime: String
        get() = formatTime(currentTime)
    
    val formattedDuration: String
        get() = formatTime(duration)
    
    private fun formatTime(seconds: Long): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return "${mins}:${secs.toString().padStart(2, '0')}"
    }
}

sealed interface VideoPlayerIntent {
    data class LoadVideo(val videoId: String) : VideoPlayerIntent
    data object Play : VideoPlayerIntent
    data object Pause : VideoPlayerIntent
    data class SeekTo(val position: Long) : VideoPlayerIntent
    data class UpdateCurrentTime(val time: Long) : VideoPlayerIntent
    data class UpdateDuration(val duration: Long) : VideoPlayerIntent
    data object SetBuffering : VideoPlayerIntent
    data object ClearBuffering : VideoPlayerIntent
}

sealed interface VideoPlayerEffect {
    data class ShowError(val message: String) : VideoPlayerEffect
}

class VideoPlayerStore(
    private val repository: VideoRepository
) : MviStore<VideoPlayerState, VideoPlayerIntent, VideoPlayerEffect>(VideoPlayerState()) {
    
    override suspend fun handleIntent(state: VideoPlayerState, intent: VideoPlayerIntent) {
        when (intent) {
            is VideoPlayerIntent.LoadVideo -> loadVideo(intent.videoId)
            VideoPlayerIntent.Play -> updateState { copy(isPlaying = true) }
            VideoPlayerIntent.Pause -> updateState { copy(isPlaying = false) }
            is VideoPlayerIntent.SeekTo -> updateState { copy(currentTime = intent.position) }
            is VideoPlayerIntent.UpdateCurrentTime -> updateState { copy(currentTime = intent.time) }
            is VideoPlayerIntent.UpdateDuration -> updateState { copy(duration = intent.duration) }
            VideoPlayerIntent.SetBuffering -> updateState { copy(isBuffering = true) }
            VideoPlayerIntent.ClearBuffering -> updateState { copy(isBuffering = false) }
        }
    }
    
    private suspend fun loadVideo(videoId: String) {
        updateState { copy(isBuffering = true, error = null) }
        
        try {
            val video = repository.getVideoById(videoId)
            if (video != null) {
                updateState { 
                    copy(
                        videoId = videoId,
                        videoTitle = video.title,
                        videoUrl = video.filePath,
                        duration = video.duration,
                        isBuffering = false
                    )
                }
            } else {
                updateState { copy(isBuffering = false, error = "ویڈیو نہیں ملی") }
                postEffect(VideoPlayerEffect.ShowError("ویڈیو نہیں ملی"))
            }
        } catch (e: Exception) {
            updateState { copy(isBuffering = false, error = e.message) }
            postEffect(VideoPlayerEffect.ShowError(e.message ?: "ویڈیو لوڈ کرنے میں خرابی"))
        }
    }
}
