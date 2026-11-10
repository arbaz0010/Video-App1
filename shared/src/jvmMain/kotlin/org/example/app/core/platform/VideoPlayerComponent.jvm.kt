package org.example.app.core.platform

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
actual fun VideoPlayerView(
    videoUrl: String,
    isPlaying: Boolean,
    onCurrentTimeUpdate: (Long) -> Unit,
    onDurationUpdate: (Long) -> Unit,
    onBufferingChange: (Boolean) -> Unit,
    modifier: Modifier
) {
    // ڈیسک ٹاپ کے لیے سادہ placeholder
    // بعد میں LibVLC یا WebView سے replace کر سکتے ہیں
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎬",
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isPlaying) "چل رہی ہے..." else "رکی ہوئی",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = videoUrl,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
    
    // Simulate playback for demo
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            var currentTime = 0L
            while (isPlaying) {
                kotlinx.coroutines.delay(1000)
                currentTime++
                onCurrentTimeUpdate(currentTime)
                onDurationUpdate(300) // 5 منٹ فرضی
            }
        }
    }
}
