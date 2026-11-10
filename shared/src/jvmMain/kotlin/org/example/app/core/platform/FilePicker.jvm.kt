package org.example.app.core.platform

import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

actual class FilePickerHelper {
    actual suspend fun pickVideoFile(): String? {
        val chooser = JFileChooser()
        chooser.fileFilter = FileNameExtensionFilter(
            "Video Files", 
            "mp4", "mkv", "avi", "mov", "webm"
        )
        
        val result = chooser.showOpenDialog(null)
        return if (result == JFileChooser.APPROVE_OPTION) {
            chooser.selectedFile.absolutePath
        } else {
            null
        }
    }
}
