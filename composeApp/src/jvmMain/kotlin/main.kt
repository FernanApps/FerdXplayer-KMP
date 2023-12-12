import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.resources.ExperimentalResourceApi
import pe.fernan.kmp.xplayer.ui.App
import java.awt.Dimension


// https://www.section.io/engineering-education/how-to-embed-compose-desktop-application-to-swing-based-application/
@OptIn(ExperimentalResourceApi::class)
fun main() = application {
    Window(
        title = "FerdXplayer-KMP",
        state = rememberWindowState(width = 800.dp, height = 450.dp),
        onCloseRequest = ::exitApplication,
        undecorated = false,
    ) {
        window.minimumSize = Dimension(800, 500)
        App()
    }
}


