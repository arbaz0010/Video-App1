package org.example.app.core.platform

import android.view.ViewGroup
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import org.example.app.core.platform.VideoPlayerView as ExpectedVideoPlayerView

@Composable
actual fun VideoPlayerView(
    videoUrl: String,
    isPlaying: Boolean,
    onCurrentTimeUpdate: (Long) -> Unit,
    onDurationUpdate: (Long) -> Unit,
    onBufferingChange: (Boolean) -> Unit,
    modifier: Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    
    val exoPlayer = remember(videoUrl) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            prepare()
        }
    }
    
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
    
        LaunchedEffect(exoPlayer) {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> onBufferingChange(true)
                    Player.STATE_READY -> {
                        onBufferingChange(false)
                        onDurationUpdate(exoPlayer.duration / 1000)
                    }
                    else -> onBufferingChange(false)
                }
            }
            
            override fun onPlayerError(error: PlaybackException) {
                onBufferingChange(false)
            }
        })
        
        // یہ launch بلاک شامل کریں تاکہ delay کام کرے
        kotlinx.coroutines.launch {
            while (true) {
                kotlinx.coroutines.delay(500)
                onCurrentTimeUpdate(exoPlayer.currentPosition / 1000)
            }
        }
    }
    
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            exoPlayer.play()
        } else {
            exoPlayer.pause()
        }
    }
    
    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                useController = true
            }
        },
        modifier = modifier
    )
}
