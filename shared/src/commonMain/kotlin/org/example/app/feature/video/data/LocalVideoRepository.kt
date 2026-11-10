package org.example.app.feature.video.data

import kotlinx.coroutines.delay
import kotlinx.datetime.Clock

class LocalVideoRepository : VideoRepository {
    
    // فی الحال hardcoded ڈیٹا - بعد میں اصل فائل سسٹم سے جوڑیں گے
    private val videos = mutableListOf(
        Video(
            id = "1",
            title = "پہلی ویڈیو",
            description = "یہ ایک ٹیسٹ ویڈیو ہے",
            filePath = "/path/to/video1.mp4",
            thumbnailPath = "/path/to/thumb1.jpg",
            duration = 125, // 2 منٹ 5 سیکنڈ
            uploadDate = Clock.System.now().toEpochMilliseconds() - 86400000 // 1 دن پہلے
        ),
        Video(
            id = "2",
            title = "دوسری ویڈیو",
            description = "دوسری ٹیسٹ ویڈیو",
            filePath = "/path/to/video2.mp4",
            thumbnailPath = null,
            duration = 340, // 5 منٹ 40 سیکنڈ
            uploadDate = Clock.System.now().toEpochMilliseconds() - 172800000 // 2 دن پہلے
        )
    )
    
    override suspend fun getAllVideos(): List<Video> {
        delay(500) // نیٹ ورک لیٹینسی کی نقل
        return videos.sortedByDescending { it.uploadDate }
    }
    
    override suspend fun getVideoById(id: String): Video? {
        delay(200)
        return videos.find { it.id == id }
    }
    
    override suspend fun uploadVideo(video: Video, fileBytes: ByteArray): Result<Unit> {
        delay(1000) // اپلوڈ کی نقل
        videos.add(video)
        return Result.success(Unit)
    }
    
    override suspend fun deleteVideo(id: String): Result<Unit> {
        delay(300)
        val removed = videos.removeAll { it.id == id }
        return if (removed) Result.success(Unit) 
               else Result.failure(Exception("Video not found"))
    }
}
