package org.example.app.feature.video.data

interface VideoRepository {
    suspend fun getAllVideos(): List<Video>
    suspend fun getVideoById(id: String): Video?
    suspend fun uploadVideo(video: Video, fileBytes: ByteArray): Result<Unit>
    suspend fun deleteVideo(id: String): Result<Unit>
}
