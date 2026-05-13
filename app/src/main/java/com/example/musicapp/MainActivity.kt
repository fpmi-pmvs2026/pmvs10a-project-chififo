package com.example.musicapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MusicAppScreen()
                }
            }
        }
    }
}

@Composable
fun MusicAppScreen() {
    var artist by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var searchResults by remember { mutableStateOf<List<Track>>(emptyList()) }
    var selectedTrack by remember { mutableStateOf<Track?>(null) }
    var lyrics by remember { mutableStateOf("") }

    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
    val repository = remember { MusicRepository() }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Music Wishlist",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                StyledTextField(
                    value = artist,
                    onValueChange = { artist = it },
                    label = "Исполнитель",
                    icon = Icons.Default.Person
                )
                Spacer(modifier = Modifier.height(12.dp))
                StyledTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Название трека",
                    icon = Icons.Default.Search
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledTonalButton(
                onClick = {
                    if (artist.isNotBlank()) {
                        isLoading = true
                        coroutineScope.launch(Dispatchers.IO) {
                            try {
                                val results = repository.searchTracks(artist)
                                withContext(Dispatchers.Main) {
                                    searchResults = results
                                    selectedTrack = null
                                    if (results.isEmpty()) {
                                        Toast.makeText(context, "Не найдено", Toast.LENGTH_SHORT).show()
                                    }
                                    isLoading = false
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Ошибка сети", Toast.LENGTH_SHORT).show()
                                    isLoading = false
                                }
                            }
                        }
                    } else {
                        Toast.makeText(context, "Введите исполнителя", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = !isLoading
            ) {
                Icon(Icons.Default.Search, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isLoading) "Поиск..." else "Найти")
            }

            Button(
                onClick = {
                    val trackToSave = selectedTrack?.copy(
                        artist = artist.ifBlank { selectedTrack?.artist ?: "" },
                        title = title.ifBlank { selectedTrack?.title ?: "" },
                        lyrics = lyrics.ifBlank { selectedTrack?.lyrics ?: "" }
                    ) ?: Track(
                        artist = artist,
                        title = title,
                        lyrics = lyrics
                    )

                    if (trackToSave.artist.isNotBlank() && trackToSave.title.isNotBlank()) {
                        dbHelper.addTrack(trackToSave)
                        Toast.makeText(context, "Сохранено!", Toast.LENGTH_SHORT).show()
                        artist = ""
                        title = ""
                        lyrics = ""
                        selectedTrack = null
                        searchResults = emptyList()
                    } else {
                        Toast.makeText(context, "Заполните поля", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Сохранить")
            }
        }

        if (searchResults.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Выберите трек:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(searchResults) { result ->
                    val isSelected = selectedTrack?.artist == result.artist &&
                            selectedTrack?.title == result.title

                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedTrack = result
                                artist = result.artist
                                title = result.title
                                lyrics = "Загрузка текста..."

                                coroutineScope.launch(Dispatchers.IO) {
                                    val fetchedLyrics = repository.fetchLyrics(result.artist, result.title)
                                    withContext(Dispatchers.Main) {
                                        lyrics = fetchedLyrics
                                        selectedTrack = selectedTrack?.copy(lyrics = fetchedLyrics)
                                    }
                                }
                            },
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outlineVariant
                            }
                        ),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = result.artworkUrl,
                                contentDescription = "Обложка",
                                modifier = Modifier.size(64.dp),
                                contentScale = ContentScale.Crop
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = result.title,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }
                                )
                                Text(
                                    text = result.artist,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (isSelected) {
                                Text(
                                    text = "Выбрано",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        OutlinedButton(
            onClick = {
                context.startActivity(Intent(context, SavedTracksActivity::class.java))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, top = 8.dp)
        ) {
            Icon(Icons.Default.List, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Моя сохраненная музыка")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}