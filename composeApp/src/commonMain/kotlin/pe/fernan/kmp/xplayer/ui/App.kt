package pe.fernan.kmp.xplayer.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import pe.fernan.kmp.xplayer.ui.components.CustomSlider
import pe.fernan.kmp.xplayer.ui.components.CustomSliderDefaults
import pe.fernan.kmp.xplayer.ui.components.layout90Rotated
import pe.fernan.kmp.xplayer.ui.player.FXPlayerStatus
import pe.fernan.kmp.xplayer.theme.AppTheme
import pe.fernan.kmp.xplayer.ui.common.LocalCurrentSize
import pe.fernan.kmp.xplayer.utils.TimeUtils
import java.util.Formatter
import java.util.Locale


@ExperimentalResourceApi
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun App() = AppTheme {

    val (player, videoLayout) = rememberVideoPlayerState()
    var newLinkVideo by remember {
        mutableStateOf("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
    }

    LaunchedEffect(player) {
        player.apply {
            prepare(newLinkVideo)
        }
    }

    val status by player.status.collectAsState(FXPlayerStatus.Idle)
    val volume by player.volume.collectAsState(1f)
    val isMuted by player.isMute.collectAsState(false)
    val duration by player.duration.collectAsState(0L)
    val currentTime by player.currentTime.collectAsState(0L)
    val isRepeated by player.isRepeated.collectAsState(false)

    //println("status: $status, $volume, $isMuted, $currentTime, $duration")
    var seek: Float by remember { mutableStateOf(0f) }
    var seeking: Boolean by remember { mutableStateOf(false) }


    val coroutineScope = rememberCoroutineScope()
    var isVisibleSurface by remember { mutableStateOf(true) }
    var jobSurface: Job? by remember { mutableStateOf(null) }

    fun setVisibleSurface(value: Boolean = true) {
        isVisibleSurface = value
    }

    LaunchedEffect(isVisibleSurface) {
        jobSurface?.cancel()
        jobSurface = coroutineScope.launch {
            if (isVisibleSurface) {
                delay(3000)
                setVisibleSurface(false)
            }
        }
    }


    var openDialog by remember { mutableStateOf(false) }
    if (openDialog) {
        AlertDialog(
            title = { Text("Open new Url") },
            text = {

                OutlinedTextField(
                    value = newLinkVideo,
                    onValueChange = { newLinkVideo = it },
                    label = { Text("Video Url") },
                    singleLine = true,
                    visualTransformation = VisualTransformation.None,
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    trailingIcon = {
                        IconButton(onClick = { newLinkVideo = "" }) {
                            val imageVector = Icons.Default.Close
                            Icon(
                                imageVector,
                                contentDescription = "Delete Video Url Text"
                            )
                        }
                    }
                )


            },
            onDismissRequest = {},
            containerColor = Color.Black.copy(0.5f),
            textContentColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            confirmButton = {
                TextButton(onClick = {
                    openDialog = false
                    setVisibleSurface(true)
                    player.prepare(newLinkVideo)
                }) {
                    Text(text = "Confirm", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    openDialog = false
                    player.play()
                }) {
                    Text(text = "Dismiss", color = Color.LightGray)
                }
            },
            modifier = Modifier
                .defaultMinSize(300.dp)
                .border(0.dp, Color.Transparent, RoundedCornerShape(0))
        )
    }




    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = { println("Box detectTapGestures :::: onPress") },
                onDoubleTap = { println("Box detectTapGestures :::: onDoubleTap") },
                onLongPress = { println("Box detectTapGestures :::: onLongPress") },
                onTap = {
                    println("Box detectTapGestures :::: onTap")
                    setVisibleSurface()
                }
            )
        }) {
        videoLayout.invoke(Modifier.fillMaxSize())

        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = isVisibleSurface,
            enter = fadeIn(),
            exit = fadeOut()
        ) {

            // BackGround Gradient in TrackBar
            TrackBar(seeking = seeking, seek = seek, duration = duration, currentTime = currentTime,
                onValueChange = {
                    seek = it
                    seeking = true
                    setVisibleSurface()
                }, onValueChangeFinished = {
                    player.seekTo((duration * seek).toLong())
                    seeking = false
                }
            )

            Header(title = "Big Buck Bunny", subTitle = "Google - 1280x720", openNewClick = {
                openDialog = !openDialog
                setVisibleSurface(false)
                player.pause()
            }, infoClick = {
                openUrl("https://github.com/FernanApps/FerdXPlayer-KMP")
            })

            Volume(
                volume = volume,
                onValueChange = {
                    player.setVolume(it)
                }, onVolumeClick = {
                    player.setMute(!isMuted)
                    if (isMuted) {
                        player.setVolume(0.5f)
                    } else {
                        player.setVolume(0f)
                    }
                    setVisibleSurface()

                })

            MediaControls(
                status = status,
                onSkipClick = {
                    if (currentTime < 10000) {
                        player.seekTo(0)
                    } else {
                        player.seekTo(currentTime - 10000)
                    }
                    setVisibleSurface()

                }, onPlayPauseClick = {
                    when (status) {
                        FXPlayerStatus.Playing -> player.pause()
                        else -> player.play()
                    }
                    setVisibleSurface()

                }, onForwardClick = {
                    if (currentTime + 10000 > duration) {
                        player.seekTo(duration)
                    } else {
                        player.seekTo(currentTime + 10000)
                    }
                    setVisibleSurface()

                }
            )

            /*
            if (status is KPlayerStatus.Error) {
                    Text("Error: ${(status as KPlayerStatus.Error).error}")
                }

             */


        }


    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun Header(
    modifier: Modifier = Modifier,
    title: String,
    subTitle: String,
    openNewClick: () -> Unit,
    infoClick: () -> Unit
) {
    Row(
        modifier = modifier.padding(25.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Top
    ) {

        IconButton(
            onClick = infoClick
        ) {
            Icon(
                painter = painterResource("ic_info.xml"),
                contentDescription = "ic_info",
                tint = Color.White
            )
        }


        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                subTitle,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Normal,
            )
            Text(title, color = Color.White, style = MaterialTheme.typography.titleMedium)
        }


        IconButton(
            onClick = openNewClick
        ) {
            Icon(
                painter = painterResource("ic_open_in_new.xml"),
                contentDescription = "ic_open_in_new",
                tint = Color.White
            )
        }
    }

}

@Composable
fun BoxScope.TrackBar(
    modifier: Modifier = Modifier,
    seeking: Boolean,
    seek: Float,
    duration: Long,
    currentTime: Long,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit
) {
    val formatBuilder = StringBuilder()
    val formatter = Formatter(formatBuilder, Locale.getDefault())

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom,
        modifier = modifier.fillMaxWidth()
            .align(Alignment.BottomCenter).background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.85f),
                        Color.Black.copy(alpha = 0.25f),
                        Color.Black.copy(alpha = 0.85f)
                    ),
                    startY = 0.3f,
                )
            ).padding(10.dp)
    ) {

        Text(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 15.dp),
            text = TimeUtils.getStringForTime(formatBuilder, formatter, currentTime),
            maxLines = 1,
            color = Color.White
        )
        CustomSlider(
            value = if (duration > 0 && !seeking) currentTime / duration.toFloat() else seek,
            modifier = Modifier.weight(1f),
            //valueRange = 0f..duration.toFloat(),
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            showLabel = seeking,
            label = {
                Card(
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Text(
                        text = TimeUtils.getStringForTime(
                            formatBuilder,
                            formatter,
                            (duration * seek).toLong()
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = Color.Black
                    )
                }
            },
            thumb = {
                CustomSliderDefaults.Thumb("", color = Color.White)
            },
            track = { sliderPositions ->
                CustomSliderDefaults.Track(
                    sliderPositions = sliderPositions,
                    trackColor = Color.DarkGray.copy(alpha = 0.5f),
                    progressColor = Color.White
                )
            }
        )
        Text(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 15.dp),
            text = TimeUtils.getStringForTime(formatBuilder, formatter, duration),
            maxLines = 1,
            color = Color.White
        )

    }
}

@ExperimentalResourceApi
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun BoxScope.MediaControls(
    modifier: Modifier = Modifier,
    status: FXPlayerStatus,
    onPlayPauseClick: () -> Unit,
    onSkipClick: () -> Unit,
    onForwardClick: () -> Unit
) {

    val windowSizeClass = calculateWindowSizeClass()
    val sizeIcon = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Medium -> 80.dp
        WindowWidthSizeClass.Expanded -> 90.dp
        else -> {
            // WindowWidthSizeClass.Compact -> 32.dp
            30.dp
        }
    }

    Row(
        modifier = modifier.fillMaxWidth()
            .align(Alignment.Center),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        if (status is FXPlayerStatus.Buffering) {
            CircularProgressIndicator()
        } else {

            val isPlay = when (status) {
                FXPlayerStatus.Playing -> true
                else -> false
            }

            IconButton(modifier = Modifier.size(sizeIcon).padding(10.dp), onClick = onSkipClick) {
                Icon(
                    painter = painterResource("netflix_skip_back.xml"),
                    contentDescription = "skip_back",
                    tint = Color.White
                )
            }
            IconButton(
                modifier = Modifier.size((sizeIcon + 20.dp)).padding(10.dp),
                onClick = onPlayPauseClick
            ) {
                Icon(
                    painter = painterResource(if (isPlay) "netflix_pause.xml" else "netflix_play.xml"),
                    contentDescription = "skip_back",
                    tint = Color.White
                )
            }
            IconButton(
                modifier = Modifier.size(sizeIcon).padding(10.dp),
                onClick = onForwardClick
            ) {
                Icon(
                    painter = painterResource("netflix_skip_forward.xml"),
                    contentDescription = "skip_back",
                    tint = Color.White
                )
            }
        }


    }
}

@ExperimentalResourceApi
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Volume(
    modifier: Modifier = Modifier,
    volume: Float,
    onValueChange: (Float) -> Unit,
    onVolumeClick: () -> Unit
) {

    val windowHeightInDp = LocalCurrentSize().height
    Box(modifier = modifier.fillMaxSize().padding(25.dp)) {

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .rotate(-90f)
                .layout90Rotated()
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                CustomSlider(
                    modifier = Modifier.width(windowHeightInDp / 3),
                    value = volume,
                    onValueChange = onValueChange,
                    thumb = {},
                    track = { sliderPositions ->
                        CustomSliderDefaults.Track(
                            sliderPositions = sliderPositions,
                            trackColor = Color.DarkGray.copy(alpha = 0.5f),
                            progressColor = Color.White,
                            height = 6.dp
                        )
                    }
                )

                /*
               ic_volume_down.xml
               ic_volume_mute.xml
               ic_volume_off.xml
               ic_volume_up.xml
            */
                IconButton(
                    modifier = Modifier,
                    onClick = onVolumeClick
                ) {
                    Icon(
                        painter = painterResource(
                            when (volume) {
                                in 0.1f..0.25f -> "ic_volume_mute.xml"
                                in 0.25f..0.7f -> "ic_volume_down.xml"
                                in 0.7f..1.0f -> "ic_volume_up.xml"
                                else -> "ic_volume_off.xml"
                            }
                        ),
                        contentDescription = "volume_up",
                        modifier = Modifier.size(24.dp).rotate(90f),
                        tint = Color.White
                    )

                }
            }


        }
    }
}


internal expect fun openUrl(url: String?)

