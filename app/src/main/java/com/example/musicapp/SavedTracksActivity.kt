package com.example.musicapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

class SavedTracksActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SavedTracksScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SavedTracksScreen() {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }

    // Состояние списка треков, чтобы UI обновлялся при удалении
    var tracks by remember { mutableStateOf(dbHelper.getAllTracks()) }

    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        Text(text = "Моя коллекция", style = MaterialTheme.typography.headlineMedium)
        Text(text = "(Удерживай трек, чтобы удалить)", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(tracks) { track ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .combinedClickable(
                            onClick = { },
                            onLongClick = {
                                dbHelper.deleteTrack(track.first) // Удаляем из БД
                                tracks = dbHelper.getAllTracks() // Обновляем список
                                Toast.makeText(context, "Трек удален", Toast.LENGTH_SHORT).show()
                            }
                        )
                ) {
                    Text(
                        text = track.second,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}