package pe.fernan.kmp.xplayer.ui.player

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.MediaPlayerComponent
import java.util.Locale
/*

private val PLAYER_ARGS = listOf(
    "--video-title=vlcj video output",
    "--no-snapshot-preview",
    "--quiet",
    "--intf=dummy"
)

val mediaPlayerFactory = remember { MediaPlayerFactory(PLAYER_ARGS) }
    val mediaPlayer = remember {
        mediaPlayerFactory
            .mediaPlayers()
            .newEmbeddedMediaPlayer()
    }
    val surface = remember {
        SkiaBitmapVideoSurface().also {
            mediaPlayer.videoSurface().set(it)
        }
    }
 */



actual class FXVideoPlayer(
    component: MediaPlayerComponent
) {
    val player get() = _player
    private val _player = component.mediaPlayer()

    private val _status = MutableStateFlow<FXPlayerStatus>(FXPlayerStatus.Idle)
    actual val status: Flow<FXPlayerStatus>
        get() = _status

    private val _volume = MutableStateFlow(1f)
    actual val volume: Flow<Float>
        get() = _volume

    private val _isMute = MutableStateFlow(false)
    actual val isMute: Flow<Boolean>
        get() = _isMute

    private val _currentTime = MutableStateFlow(0L)
    actual val currentTime: Flow<Long>
        get() = _currentTime

    private val _duration = MutableStateFlow(0L)
    actual val duration: Flow<Long>
        get() = _duration

    private val _isRepeated = MutableStateFlow(false)
    actual val isRepeated: Flow<Boolean>
        get() = _isRepeated

    private val eventAdapter = object : MediaPlayerEventAdapter() {
        override fun buffering(mediaPlayer: MediaPlayer?, newCache: Float) {
            if (newCache == 100.0f) {
                _status.value = if (mediaPlayer?.status()?.isPlaying == true) FXPlayerStatus.Playing else FXPlayerStatus.Paused
            } else {
                _status.value = FXPlayerStatus.Buffering
            }
        }

        override fun playing(mediaPlayer: MediaPlayer?) {
            _status.value = FXPlayerStatus.Playing
        }

        override fun paused(mediaPlayer: MediaPlayer?) {
            _status.value = FXPlayerStatus.Paused
        }

        /**
         * Waiting for this event may be more reliable than using playing(MediaPlayer) or videoOutput(MediaPlayer, int)
         * in some cases (logo and marquee already mentioned, also setting audio tracks, sub-title tracks and so on).
         */
        override fun mediaPlayerReady(mediaPlayer: MediaPlayer?) {
            _status.value = FXPlayerStatus.Playing
            _duration.value = _player.status().length()
        }

        override fun timeChanged(mediaPlayer: MediaPlayer?, newTime: Long) {
            _currentTime.value = newTime
        }

        override fun finished(mediaPlayer: MediaPlayer?) {
            _status.value = FXPlayerStatus.Ended
        }


        override fun error(mediaPlayer: MediaPlayer?) {

            _status.value = FXPlayerStatus.Error(
                Error(
                    "Failed to load media ${
                        mediaPlayer?.media()?.info()?.mrl()
                    }"
                )
            )
        }
    }

    private var currentDataSource: Any? = null
    actual fun prepare(dataSource: Any, playWhenReady: Boolean) {
        currentDataSource = dataSource
        _status.value = FXPlayerStatus.Preparing
        _player.events().addMediaPlayerEventListener(eventAdapter)
        //_player.media().play(dataSource.toString(), ":http-referrer=referer")

        if (playWhenReady) {
            player.media().play(dataSource.toString())
        } else {
            player.media().prepare(dataSource.toString())
        }

        _duration.value = _player.status().length()
        _status.value = FXPlayerStatus.Ready
    }

    actual fun play() {
        if (_status.value is FXPlayerStatus.Error) {
            currentDataSource?.let {
                prepare(dataSource = it, playWhenReady = true)
            }
        } else {
            _player.controls().play()
        }

    }

    actual fun pause() {
        _player.controls().pause()
    }

    actual fun stop() {
        _player.controls().stop()
    }

    actual fun release() {
        _player.release()
        _status.value = FXPlayerStatus.Released
    }

    actual fun seekTo(position: Long) {
        _player.controls()?.setTime(position)
        _currentTime.value = position
    }

    actual fun setMute(mute: Boolean) {
        _player.audio().setVolume(if (mute) 0 else _volume.value.toVLCVolume())
        _isMute.value = mute
    }

    actual fun setVolume(volume: Float) {
        volume.coerceIn(0f, 1f).let {
            _player.audio().setVolume(it.toVLCVolume())
            _volume.value = it
        }
    }

    actual fun setRepeat(isRepeat: Boolean) {
        _player.controls().repeat = isRepeat
        _isRepeated.value = isRepeat
    }

    private fun Float.toVLCVolume() = (this * 200).toInt()
}

private fun Any.mediaPlayer(): MediaPlayer {

    return when (this) {
        is CallbackMediaPlayerComponent -> mediaPlayer()
        is EmbeddedMediaPlayerComponent -> {
            val mediaPlayerFactory = MediaPlayerFactory(PLAYER_ARGS)
            val mediaPlayer =
                mediaPlayerFactory
                    .mediaPlayers()
                    .newEmbeddedMediaPlayer()

            mediaPlayer
            //mediaPlayer()
        }
        else -> throw IllegalArgumentException("You can only call mediaPlayer() on vlcj player component")
    }
}

// Same as MediaPlayerComponentDefaults.EMBEDDED_MEDIA_PLAYER_ARGS
private val PLAYER_ARGS = listOf(
    "--video-title=vlcj video output",
    "--no-snapshot-preview",
    "--quiet",
    "--intf=dummy"
)


fun defaultComponent(): MediaPlayerComponent = if (isMacOS()) {
    CallbackMediaPlayerComponent()
} else {

    EmbeddedMediaPlayerComponent()
}

private fun isMacOS(): Boolean {
    val os = System.getProperty("os.name", "generic").lowercase(Locale.ENGLISH)
    return os.indexOf("mac") >= 0 || os.indexOf("darwin") >= 0
}