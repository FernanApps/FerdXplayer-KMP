package pe.fernan.kmp.xplayer.ui.player

import kotlinx.coroutines.flow.Flow

public expect class FXVideoPlayer {
    val status: Flow<FXPlayerStatus>
    val volume: Flow<Float>
    val isMute: Flow<Boolean>
    val currentTime: Flow<Long>
    val duration: Flow<Long>
    val isRepeated: Flow<Boolean>
    fun prepare(dataSource: Any, playWhenReady: Boolean = true)
    fun play()
    fun pause()
    fun stop()
    fun release()
    fun seekTo(position: Long)
    fun setMute(mute: Boolean)
    fun setVolume(volume: Float)
    fun setRepeat(isRepeat: Boolean)
}



