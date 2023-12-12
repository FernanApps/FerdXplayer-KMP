package pe.fernan.kmp.xplayer.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
internal actual fun LocalCurrentSize() = CurrentWindowsSize(
    width = LocalConfiguration.current.screenWidthDp.dp,
    height = LocalConfiguration.current.screenHeightDp.dp
)