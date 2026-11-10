package org.example.app.feature.videoplayer.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.app.core.mvi.CollectSideEffects
import org.example.app.core.platform.VideoPlayerView
import org.example.app.feature.videoplayer.presentation.VideoPlayerEffect
import org.example.app.feature.videoplayer.presentation.VideoPlayerIntent
import org.example.app.feature.videoplayer.presentation.VideoPlayerStore
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    videoId: String,
    onBack: () -> Unit,
    store: VideoPlayerStore = koinInject()
) {
    val state by store.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(videoId) {
        store.dispatch(VideoPlayerIntent.LoadVideo(videoId))
    }
    
    CollectSideEffects(store.effects) { effect ->
        when (effect) {
            is VideoPlayerEffect.ShowError -> {
                snackbarHostState.showSnackbar(effect.message)
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.videoTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ویڈیو پلیر
            if (state.videoUrl.isNotEmpty()) {
                VideoPlayerView(
                    videoUrl = state.videoUrl,
                    isPlaying = state.isPlaying,
                    onCurrentTimeUpdate = { time ->
                        store.dispatch(VideoPlayerIntent.UpdateCurrentTime(time))
                    },
                    onDurationUpdate = { duration ->
                        store.dispatch(VideoPlayerIntent.UpdateDuration(duration))
                    },
                    onBufferingChange = { isBuffering ->
                        if (isBuffering) {
                            store.dispatch(VideoPlayerIntent.SetBuffering)
                        } else {
                            store.dispatch(VideoPlayerIntent.ClearBuffering)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            } else if (state.isBuffering) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            // کنٹرولز
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ٹائٹل
                Text(
                    text = state.videoTitle,
                    style = MaterialTheme.typography.headlineSmall
                )
                
                // پروگریس بار
                Column {
                    Slider(
                        value = state.progress,
                        onValueChange = { newProgress ->
                            val newPosition = (newProgress * state.duration).toLong()
                            store.dispatch(VideoPlayerIntent.SeekTo(newPosition))
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = state.formattedCurrentTime,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = state.formattedDuration,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                
                // پلے/پاز بٹنز
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (state.isPlaying) {
                                store.dispatch(VideoPlayerIntent.Pause)
                            } else {
                                store.dispatch(VideoPlayerIntent.Play)
                            }
                        },
                        enabled = state.videoUrl.isNotEmpty() && !state.isBuffering
                    ) {
                        Text(
                            text = if (state.isPlaying) "⏸️" else "▶️",
                            style = MaterialTheme.typography.displayMedium
                        )
                    }
                }
                
                // بفرنگ انڈیکیٹر
                if (state.isBuffering) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
