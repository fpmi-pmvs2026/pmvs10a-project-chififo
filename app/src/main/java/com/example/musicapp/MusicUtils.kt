package com.example.musicapp

object MusicUtils {

    fun isTrackSelected(selectedTrack: Track?, result: Track): Boolean {
        return selectedTrack?.artist == result.artist &&
                selectedTrack?.title == result.title
    }

    fun canSaveTrack(track: Track): Boolean {
        return track.artist.isNotBlank() && track.title.isNotBlank()
    }

    fun normalizeLyrics(text: String?): String {
        if (text.isNullOrBlank()) return "Текст песни не найден."
        return text.trim()
    }
}