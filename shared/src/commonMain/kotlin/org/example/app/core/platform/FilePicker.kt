package org.example.app.core.platform

expect class FilePickerHelper() {
    suspend fun pickVideoFile(): String?
}
