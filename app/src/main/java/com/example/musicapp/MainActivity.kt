package com.example.musicapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
            MaterialTheme {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicAppScreen() {
    var artist by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }

    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
    val coroutineScope = rememberCoroutineScope() // Для многопоточности

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(text = "Поиск и добавление трека", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = artist,
            onValueChange = { artist = it },
            label = { Text("Исполнитель (напр. Beatles)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Название трека") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка запроса к API
        Button(
            onClick = {
                if (artist.isNotBlank()) {
                    // Запускаем фоновый поток (IO)
                    coroutineScope.launch(Dispatchers.IO) {
                        try {
                            val url = "https://itunes.apple.com/search?term=${artist.replace(" ", "+")}&entity=song&limit=1"
                            val response = URL(url).readText()
                            val json = JSONObject(response)
                            val results = json.getJSONArray("results")

                            if (results.length() > 0) {
                                val trackName = results.getJSONObject(0).getString("trackName")
                                val artistName = results.getJSONObject(0).getString("artistName")

                                // Возвращаемся в главный поток для обновления UI
                                withContext(Dispatchers.Main) {
                                    title = trackName
                                    artist = artistName
                                    Toast.makeText(context, "Найдено!", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Ничего не найдено", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Ошибка сети: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Введите исполнителя для поиска", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("Найти хит в интернете")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (artist.isNotBlank() && title.isNotBlank()) {
                    dbHelper.addTrack(artist, title)
                    Toast.makeText(context, "Трек сохранен в БД!", Toast.LENGTH_SHORT).show()
                    artist = ""
                    title = ""
                } else {
                    Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сохранить в БД")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val intent = android.content.Intent(context, SavedTracksActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
        ) {
            Text("Моя сохраненная музыка")
        }
    }
}