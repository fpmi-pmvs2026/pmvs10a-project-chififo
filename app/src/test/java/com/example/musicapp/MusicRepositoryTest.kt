package com.example.musicapp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MusicRepositoryTest {

    private val repository = MusicRepository()

    @Test
    fun buildSearchUrl_encodesSpacesCorrectly() {
        val url = repository.buildSearchUrl("Linkin Park")
        assertEquals(
            "https://itunes.apple.com/search?term=Linkin+Park&entity=song&limit=10",
            url
        )
    }

    @Test
    fun buildSearchUrl_encodesSpecialCharacters() {
        val url = repository.buildSearchUrl("AC/DC")
        assertTrue(url.contains("AC%2FDC"))
    }

    @Test
    fun formatTime_returnsZeroForEmptyTime() {
        assertEquals("00:00", formatTime(0))
    }

    @Test
    fun formatTime_formatsSecondsCorrectly() {
        assertEquals("00:59", formatTime(59_000))
    }

    @Test
    fun formatTime_formatsMinutesCorrectly() {
        assertEquals("02:05", formatTime(125_000))
    }

    @Test
    fun canSaveTrack_returnsTrueForValidTrack() {
        val track = Track(artist = "Muse", title = "Starlight")
        assertTrue(MusicUtils.canSaveTrack(track))
    }

    @Test
    fun canSaveTrack_returnsFalseForBlankArtist() {
        val track = Track(artist = "", title = "Starlight")
        assertFalse(MusicUtils.canSaveTrack(track))
    }

    @Test
    fun canSaveTrack_returnsFalseForBlankTitle() {
        val track = Track(artist = "Muse", title = "")
        assertFalse(MusicUtils.canSaveTrack(track))
    }

    @Test
    fun isTrackSelected_returnsTrueForSameTrack() {
        val selected = Track(artist = "Muse", title = "Starlight")
        val result = Track(artist = "Muse", title = "Starlight")
        assertTrue(MusicUtils.isTrackSelected(selected, result))
    }

    @Test
    fun isTrackSelected_returnsFalseForDifferentTrack() {
        val selected = Track(artist = "Muse", title = "Uprising")
        val result = Track(artist = "Muse", title = "Starlight")
        assertFalse(MusicUtils.isTrackSelected(selected, result))
    }

    @Test
    fun normalizeLyrics_returnsFallbackForNull() {
        assertEquals("Текст песни не найден.", MusicUtils.normalizeLyrics(null))
    }

    @Test
    fun normalizeLyrics_returnsFallbackForBlank() {
        assertEquals("Текст песни не найден.", MusicUtils.normalizeLyrics("   "))
    }

    @Test
    fun normalizeLyrics_trimsText() {
        assertEquals("Hello world", MusicUtils.normalizeLyrics("  Hello world  "))
    }
}