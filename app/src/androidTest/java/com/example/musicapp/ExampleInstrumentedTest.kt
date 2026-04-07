package com.example.musicapp

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MusicAppUITest {

    // Правило для запуска Compose активности
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun uiElementsAreDisplayedOnMainScreen() {
        // Проверяем, что основные элементы интерфейса существуют на экране
        composeTestRule.onNodeWithText("Music Wishlist").assertExists()
        composeTestRule.onNodeWithText("Исполнитель").assertExists()
        composeTestRule.onNodeWithText("Название трека").assertExists()
        composeTestRule.onNodeWithText("Найти").assertExists()
        composeTestRule.onNodeWithText("Сохранить").assertExists()
        composeTestRule.onNodeWithText("Моя сохраненная музыка").assertExists()
    }
}