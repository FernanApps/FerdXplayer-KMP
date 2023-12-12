package pe.fernan.kmp.xplayer.utils

import java.util.Formatter

object TimeUtils {
    fun getStringForTime(
        builder: java.lang.StringBuilder,
        formatter: Formatter,
        timeMs: Long
    ): String {
        var timeMs = timeMs
        if (timeMs == Long.MIN_VALUE + 1) {
            timeMs = 0
        }
        val totalSeconds = (timeMs + 500) / 1000
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        builder.setLength(0)
        return if (hours > 0) formatter.format("%d:%02d:%02d", hours, minutes, seconds)
            .toString() else formatter.format("%02d:%02d", minutes, seconds).toString()
    }
}