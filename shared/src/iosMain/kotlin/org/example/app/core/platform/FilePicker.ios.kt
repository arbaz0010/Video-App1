package org.example.app.core.platform

actual class FilePickerHelper {
    actual suspend fun pickVideoFile(): String? {
        // iOS میں SwiftUI کا fileImporter استعمال کریں گے
        return null // UI layer میں handle ہوگا
    }
}
