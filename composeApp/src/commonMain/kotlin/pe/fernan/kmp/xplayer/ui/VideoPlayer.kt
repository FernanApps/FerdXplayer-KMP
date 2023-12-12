package pe.fernan.kmp.xplayer.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import pe.fernan.kmp.xplayer.ui.player.FXVideoPlayer

@Composable
expect fun rememberVideoPlayerState(): VideoPlayerState

data class VideoPlayerState(
    val player: FXVideoPlayer,
    val content: @Composable (Modifier) -> Unit
)