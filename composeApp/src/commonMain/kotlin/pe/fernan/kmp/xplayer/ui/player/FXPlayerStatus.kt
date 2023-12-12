package pe.fernan.kmp.xplayer.ui.player

sealed interface FXPlayerStatus {
    data object Idle : FXPlayerStatus
    data object Preparing : FXPlayerStatus
    data object Ready : FXPlayerStatus
    data object Buffering : FXPlayerStatus
    data object Playing : FXPlayerStatus
    data object Paused : FXPlayerStatus
    data object Ended : FXPlayerStatus
    data class Error(val error: Throwable) : FXPlayerStatus
    data object Released : FXPlayerStatus
}