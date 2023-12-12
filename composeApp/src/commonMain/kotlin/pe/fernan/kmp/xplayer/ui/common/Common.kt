package pe.fernan.kmp.xplayer.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

data class CurrentWindowsSize(val height: Dp, val width: Dp)

@Composable
internal expect fun LocalCurrentSize(): CurrentWindowsSize