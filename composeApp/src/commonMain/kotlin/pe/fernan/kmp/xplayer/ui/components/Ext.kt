package pe.fernan.kmp.xplayer.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }


@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }


fun Modifier.layout90Rotated() =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.height, placeable.width) {
            placeable.place(-placeable.height, (placeable.width - placeable.height) / 2)
        }
    }


/*
Rotate Example
Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray))
        {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(Color.Green)
            ) { Text("Player 1") }

            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .rotate(90f)
                    .layout90Rotated()
                    .background(Color.Green)
            ) { Text("Player 2") }

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .rotate(180f)
                    .background(Color.Green)
            ) { Text("Player 3") }

            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .rotate(-90f)
                    .layout90Rotated()
                    .background(Color.Green)
            ) { Text("Player 4") }

        }

 */