package com.example.musicapp

import org.json.JSONObject
import java.net.URLEncoder
import java.net.URL
import java.nio.charset.StandardCharsets

class MusicRepository {

    fun buildSearchUrl(query: String): String {
        val encoded = URLEncoder.encode(query, StandardCharsets.UTF_8.toString())
        return "https://itunes.apple.com/search?term=$encoded&entity=song&limit=10"
    }

    fun searchTracks(query: String): List<Track> {
        val response = URL(buildSearchUrl(query)).readText()
        val json = JSONObject(response)
        val resultsArray = json.getJSONArray("results")
        val parsedList = mutableListOf<Track>()

        for (i in 0 until resultsArray.length()) {
            val obj = resultsArray.getJSONObject(i)
            parsedList.add(
                Track(
                    artist = obj.optString("artistName"),
                    title = obj.optString("trackName"),
                    artworkUrl = obj.optString("artworkUrl100"),
                    audioUrl = obj.optString("previewUrl"),
                    lyrics = ""
                )
            )
        }

        return parsedList
    }

    fun fetchLyrics(artist: String, title: String): String {
        return try {
            val encodedArtist = URLEncoder.encode(artist, StandardCharsets.UTF_8.toString())
            val encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.toString())
            val url = "https://api.lyrics.ovh/v1/$encodedArtist/$encodedTitle"
            val response = URL(url).readText()
            val json = JSONObject(response)
            json.optString("lyrics", "Текст песни не найден.")
                .ifBlank { "Текст песни не найден." }
        } catch (e: Exception) {
            "Текст песни не найден."
        }
    }
}