package pe.fernan.kmp.xplayer.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ui.StyledPlayerView
import pe.fernan.kmp.xplayer.ui.player.FXVideoPlayer

@Composable
actual fun rememberVideoPlayerState(): VideoPlayerState {
    val context = LocalContext.current
    return remember {
        val view = StyledPlayerView(context)
        VideoPlayerState(
            player = FXVideoPlayer(view),
            content = {
                AndroidView(
                    factory = {
                        view
                    },
                    modifier = it
                )
            }
        )
    }
}