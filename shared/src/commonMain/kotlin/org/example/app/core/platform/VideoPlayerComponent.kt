package org.example.app.core.platform

import androidx.compose.runtime.Composable

@Composable
expect fun VideoPlayerView(
    videoUrl: String,
    isPlaying: Boolean,
    onCurrentTimeUpdate: (Long) -> Unit,
    onDurationUpdate: (Long) -> Unit,
    onBufferingChange: (Boolean) -> Unit,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
)
