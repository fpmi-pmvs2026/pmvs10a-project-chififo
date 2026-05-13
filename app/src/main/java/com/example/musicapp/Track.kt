package com.example.musicapp

data class Track(
    val id: Int = 0,
    val artist: String,
    val title: String,
    val artworkUrl: String = "",
    val audioUrl: String = "",
    val lyrics: String = ""
)