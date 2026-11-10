package org.example.app.core.platform

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

actual class FilePickerHelper {
    actual suspend fun pickVideoFile(): String? {
        // Compose میں ActivityResultLauncher استعمال کریں گے
        return null // UI layer میں handle ہوگا
    }
}

@Composable
fun rememberVideoFilePicker(onFilePicked: (String) -> Unit) {
    val context = LocalContext.current
    var pickedUri by remember { mutableStateOf<Uri?>(null) }
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            pickedUri = it
            // URI کو file path میں convert کریں
            val path = getRealPathFromUri(context, it)
            onFilePicked(path ?: it.toString())
        }
    }
    
    // Launcher کو واپس کریں تاکہ UI سے call کر سکیں
    remember {
        object {
            fun launch() = launcher.launch("video/*")
        }
    }
}

private fun getRealPathFromUri(context: Context, uri: Uri): String? {
    // Simplified - اصل implementation میں ContentResolver استعمال کریں
    return uri.path
}
