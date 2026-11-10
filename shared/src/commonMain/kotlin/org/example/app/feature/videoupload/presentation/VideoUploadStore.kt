package org.example.app.feature.videoupload.presentation

import kotlinx.datetime.Clock
import org.example.app.core.mvi.MviStore
import org.example.app.feature.video.data.Video
import org.example.app.feature.video.data.VideoRepository

data class VideoUploadState(
    val title: String = "",
    val description: String = "",
    val selectedFilePath: String? = null,
    val isUploading: Boolean = false,
    val uploadProgress: Float = 0f,
    val error: String? = null
)

sealed interface VideoUploadIntent {
    data class UpdateTitle(val title: String) : VideoUploadIntent
    data class UpdateDescription(val description: String) : VideoUploadIntent
    data class FileSelected(val filePath: String) : VideoUploadIntent
    data object UploadVideo : VideoUploadIntent
    data object Reset : VideoUploadIntent
}

sealed interface VideoUploadEffect {
    data object UploadSuccess : VideoUploadEffect
    data class ShowError(val message: String) : VideoUploadEffect
}

class VideoUploadStore(
    private val repository: VideoRepository
) : MviStore<VideoUploadState, VideoUploadIntent, VideoUploadEffect>(VideoUploadState()) {
    
    override suspend fun handleIntent(state: VideoUploadState, intent: VideoUploadIntent) {
        when (intent) {
            is VideoUploadIntent.UpdateTitle -> updateState { copy(title = intent.title) }
            is VideoUploadIntent.UpdateDescription -> updateState { copy(description = intent.description) }
            is VideoUploadIntent.FileSelected -> updateState { copy(selectedFilePath = intent.filePath) }
            is VideoUploadIntent.UploadVideo -> uploadVideo(state)
            is VideoUploadIntent.Reset -> updateState { VideoUploadState() }
        }
    }
    
    private suspend fun uploadVideo(state: VideoUploadState) {
        if (state.title.isBlank()) {
            postEffect(VideoUploadEffect.ShowError("عنوان درکار ہے"))
            return
        }
        
        if (state.selectedFilePath == null) {
            postEffect(VideoUploadEffect.ShowError("ویڈیو فائل منتخب کریں"))
            return
        }
        
        updateState { copy(isUploading = true, uploadProgress = 0f, error = null) }
        
        try {
            val video = Video(
                id = Clock.System.now().toEpochMilliseconds().toString(),
                title = state.title,
                description = state.description,
                filePath = state.selectedFilePath,
                thumbnailPath = null,
                duration = 0,
                uploadDate = Clock.System.now().toEpochMilliseconds()
            )
            
            val result = repository.uploadVideo(video, ByteArray(0))
            
            result.fold(
                onSuccess = {
                    updateState { copy(isUploading = false, uploadProgress = 1f) }
                    postEffect(VideoUploadEffect.UploadSuccess)
                    dispatch(VideoUploadIntent.Reset)
                },
                onFailure = { error ->
                    updateState { copy(isUploading = false, error = error.message) }
                    postEffect(VideoUploadEffect.ShowError(error.message ?: "اپلوڈ ناکام"))
                }
            )
        } catch (e: Exception) {
            updateState { copy(isUploading = false, error = e.message) }
            postEffect(VideoUploadEffect.ShowError(e.message ?: "نامعلوم خرابی"))
        }
    }
}
