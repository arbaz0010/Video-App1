package org.example.app.feature.videolist.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.example.app.core.mvi.CollectSideEffects
import org.example.app.feature.videolist.presentation.VideoListEffect
import org.example.app.feature.videolist.presentation.VideoListIntent
import org.example.app.feature.videolist.presentation.VideoListStore
import org.example.app.feature.video.data.Video
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoListScreen(
    onVideoClick: (String) -> Unit,
    onUploadClick: () -> Unit,
    store: VideoListStore = koinInject()
) {
    val state by store.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    CollectSideEffects(store.effects) { effect ->
        when (effect) {
            is VideoListEffect.ShowError -> {
                snackbarHostState.showSnackbar(effect.message)
            }
            is VideoListEffect.ShowSuccess -> {
                snackbarHostState.showSnackbar(effect.message)
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ویڈیوز") },
                actions = {
                    IconButton(onClick = { store.dispatch(VideoListIntent.RefreshVideos) }) {
                        Text("↻")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onUploadClick) {
                Text("+")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading && state.videos.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                state.error != null && state.videos.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(state.error ?: "خرابی")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { store.dispatch(VideoListIntent.LoadVideos) }) {
                            Text("دوبارہ کوشش کریں")
                        }
                    }
                }
                state.videos.isEmpty() -> {
                    Text(
                        "کوئی ویڈیو نہیں",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.videos, key = { it.id }) { video ->
                            VideoItem(
                                video = video,
                                onClick = { onVideoClick(video.id) },
                                onDelete = { store.dispatch(VideoListIntent.DeleteVideo(video.id)) }
                            )
                        }
                    }
                }
            }
            
            if (state.isLoading && state.videos.isNotEmpty()) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )
            }
        }
    }
}

@Composable
private fun VideoItem(
    video: Video,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = video.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${video.formattedDuration} • ${video.formattedDate}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            IconButton(onClick = onDelete) {
                Text("🗑️")
            }
        }
    }
}
