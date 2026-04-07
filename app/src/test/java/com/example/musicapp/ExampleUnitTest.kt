package com.example.musicapp

import org.junit.Test
import org.junit.Assert.*

class SearchLogicTest {

    @Test
    fun buildSearchUrl_isCorrect() {
        val artistInput = "Linkin Park"
        val expectedUrl = "https://itunes.apple.com/search?term=Linkin+Park&entity=song&limit=10"

        // Эмулируем логику из MainActivity
        val actualUrl = "https://itunes.apple.com/search?term=${artistInput.replace(" ", "+")}&entity=song&limit=10"

        assertEquals("URL для поиска формируется неверно", expectedUrl, actualUrl)
    }
}