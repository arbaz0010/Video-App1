package org.example.app.feature.video.data

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Video(
    val id: String,
    val title: String,
    val description: String,
    val filePath: String, // لوکل فائل پاتھ (بعد میں URL بن جائے گا)
    val thumbnailPath: String?,
    val duration: Long, // سیکنڈز میں
    val uploadDate: Long // timestamp
) {
    val formattedDuration: String
        get() {
            val minutes = duration / 60
            val seconds = duration % 60
            return "${minutes}:${seconds.toString().padStart(2, '0')}"
        }
    
    val formattedDate: String
        get() {
            // سادہ formatting - بعد میں بہتر بنا سکتے ہیں
            return Instant.fromEpochMilliseconds(uploadDate).toString().take(10)
        }
}
