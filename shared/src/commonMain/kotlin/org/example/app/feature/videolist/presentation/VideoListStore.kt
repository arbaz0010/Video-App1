package org.example.app.feature.videolist.presentation

import org.example.app.core.mvi.MviStore
import org.example.app.feature.video.data.Video
import org.example.app.feature.video.data.VideoRepository

data class VideoListState(
    val videos: List<Video> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface VideoListIntent {
    data object LoadVideos : VideoListIntent
    data object RefreshVideos : VideoListIntent
    data class DeleteVideo(val id: String) : VideoListIntent
}

sealed interface VideoListEffect {
    data class ShowError(val message: String) : VideoListEffect
    data class ShowSuccess(val message: String) : VideoListEffect
}

class VideoListStore(
    private val repository: VideoRepository
) : MviStore<VideoListState, VideoListIntent, VideoListEffect>(VideoListState()) {
    
    init {
        dispatch(VideoListIntent.LoadVideos)
    }
    
    override suspend fun handleIntent(state: VideoListState, intent: VideoListIntent) {
        when (intent) {
            is VideoListIntent.LoadVideos -> loadVideos()
            is VideoListIntent.RefreshVideos -> loadVideos(isRefresh = true)
            is VideoListIntent.DeleteVideo -> deleteVideo(intent.id)
        }
    }
    
    private suspend fun loadVideos(isRefresh: Boolean = false) {
        updateState { copy(isLoading = true, error = null) }
        
        try {
            val videos = repository.getAllVideos()
            updateState { 
                copy(
                    videos = videos,
                    isLoading = false,
                    error = null
                )
            }
        } catch (e: Exception) {
            updateState { 
                copy(
                    isLoading = false,
                    error = e.message ?: "ویڈیوز لوڈ کرنے میں خرابی"
                )
            }
            postEffect(VideoListEffect.ShowError(e.message ?: "نامعلوم خرابی"))
        }
    }
    
    private suspend fun deleteVideo(id: String) {
        try {
            val result = repository.deleteVideo(id)
            result.fold(
                onSuccess = {
                    updateState { copy(videos = videos.filter { it.id != id }) }
                    postEffect(VideoListEffect.ShowSuccess("ویڈیو حذف ہو گئی"))
                },
                onFailure = { error ->
                    postEffect(VideoListEffect.ShowError(error.message ?: "حذف کرنے میں خرابی"))
                }
            )
        } catch (e: Exception) {
            postEffect(VideoListEffect.ShowError(e.message ?: "نامعلوم خرابی"))
        }
    }
}
