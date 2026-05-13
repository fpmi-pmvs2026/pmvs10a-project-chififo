package com.example.musicapp

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay

@SuppressLint("DefaultLocale")
@Composable
fun AudioPlayer(
    audioUrl: String,
    modifier: Modifier = Modifier
) {
    if (audioUrl.isBlank()) {
        Text(
            text = "Аудио недоступно для этого трека",
            style = MaterialTheme.typography.bodyMedium
        )
        return
    }

    val context = LocalContext.current
    val player = remember(audioUrl) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(audioUrl))
            prepare()
        }
    }

    var isPlaying by remember { mutableStateOf(false) }
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var duration by remember { mutableLongStateOf(0L) }
    var currentPosition by remember { mutableLongStateOf(0L) }

    DisposableEffect(player) {
        onDispose {
            player.release()
        }
    }

    LaunchedEffect(player) {
        while (true) {
            duration = if (player.duration > 0) player.duration else 0L
            currentPosition = if (player.currentPosition > 0) player.currentPosition else 0L
            sliderPosition = if (duration > 0) {
                currentPosition.toFloat() / duration.toFloat()
            } else {
                0f
            }
            isPlaying = player.isPlaying
            delay(500)
        }
    }

    ElevatedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Плеер",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Slider(
                value = sliderPosition,
                onValueChange = { value ->
                    sliderPosition = value
                },
                onValueChangeFinished = {
                    if (duration > 0) {
                        val newPosition = (sliderPosition * duration).toLong()
                        player.seekTo(newPosition)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatTime(currentPosition))
                Text(formatTime(duration))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        val target = (player.currentPosition - 10_000).coerceAtLeast(0L)
                        player.seekTo(target)
                    }
                ) {
                    Text("-10 сек")
                }

                Button(
                    onClick = {
                        if (player.isPlaying) {
                            player.pause()
                        } else {
                            player.play()
                        }
                        isPlaying = player.isPlaying
                    }
                ) {
                    Text(if (isPlaying) "Пауза" else "Play")
                }

                TextButton(
                    onClick = {
                        val target = (player.currentPosition + 10_000).coerceAtMost(
                            if (player.duration > 0) player.duration else Long.MAX_VALUE
                        )
                        player.seekTo(target)
                    }
                ) {
                    Text("+10 сек")
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
fun formatTime(timeMs: Long): String {
    if (timeMs <= 0L) return "00:00"
    val totalSeconds = timeMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}