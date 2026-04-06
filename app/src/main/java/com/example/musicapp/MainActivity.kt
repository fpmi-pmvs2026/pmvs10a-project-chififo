package com.example.musicapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

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

    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
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

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilledTonalButton(
                onClick = {
                    if (artist.isNotBlank()) {
                        isLoading = true
                        coroutineScope.launch(Dispatchers.IO) {
                            try {
                                val url = "https://itunes.apple.com/search?term=${artist.replace(" ", "+")}&entity=song&limit=1"
                                val response = URL(url).readText()
                                val json = JSONObject(response)
                                val results = json.getJSONArray("results")
                                withContext(Dispatchers.Main) {
                                    if (results.length() > 0) {
                                        title = results.getJSONObject(0).getString("trackName")
                                        artist = results.getJSONObject(0).getString("artistName")
                                        Toast.makeText(context, "Найдено!", Toast.LENGTH_SHORT).show()
                                    } else {
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
                    if (artist.isNotBlank() && title.isNotBlank()) {
                        dbHelper.addTrack(artist, title)
                        Toast.makeText(context, "Сохранено!", Toast.LENGTH_SHORT).show()
                        artist = ""
                        title = ""
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

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = {
                context.startActivity(Intent(context, SavedTracksActivity::class.java))
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Icon(Icons.Default.List, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Моя сохраненная музыка")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyledTextField(value: String, onValueChange: (String) -> Unit, label: String, icon: ImageVector) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}