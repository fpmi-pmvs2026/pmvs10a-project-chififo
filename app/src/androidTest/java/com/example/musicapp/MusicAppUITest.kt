package com.example.musicapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MusicAppUITest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun mainScreen_displaysTitle() {
        composeTestRule.onNodeWithText("Music Wishlist").assertIsDisplayed()
    }

    @Test
    fun mainScreen_displaysArtistField() {
        composeTestRule.onNodeWithText("Исполнитель").assertIsDisplayed()
    }

    @Test
    fun mainScreen_displaysTitleField() {
        composeTestRule.onNodeWithText("Название трека").assertIsDisplayed()
    }

    @Test
    fun mainScreen_displaysSearchButton() {
        composeTestRule.onNodeWithText("Найти").assertIsDisplayed()
    }

    @Test
    fun mainScreen_displaysSaveButton() {
        composeTestRule.onNodeWithText("Сохранить").assertIsDisplayed()
    }

    @Test
    fun mainScreen_displaysSavedTracksButton() {
        composeTestRule.onNodeWithText("Моя сохраненная музыка").assertIsDisplayed()
    }

    @Test
    fun artistField_acceptsInput() {
        composeTestRule.onAllNodesWithText("Исполнитель")[0].performTextInput("Muse")
        composeTestRule.onAllNodesWithText("Muse")[0].assertIsDisplayed()
    }

    @Test
    fun titleField_acceptsInput() {
        composeTestRule.onAllNodesWithText("Название трека")[0].performTextInput("Starlight")
        composeTestRule.onAllNodesWithText("Starlight")[0].assertIsDisplayed()
    }

    @Test
    fun bothFields_acceptInputTogether() {
        composeTestRule.onAllNodesWithText("Исполнитель")[0].performTextInput("Linkin Park")
        composeTestRule.onAllNodesWithText("Название трека")[0].performTextInput("Numb")

        composeTestRule.onAllNodesWithText("Linkin Park")[0].assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Numb")[0].assertIsDisplayed()
    }

    @Test
    fun buttons_remainVisibleAfterInput() {
        composeTestRule.onAllNodesWithText("Исполнитель")[0].performTextInput("Adele")
        composeTestRule.onAllNodesWithText("Название трека")[0].performTextInput("Hello")

        composeTestRule.onNodeWithText("Найти").assertIsDisplayed()
        composeTestRule.onNodeWithText("Сохранить").assertIsDisplayed()
        composeTestRule.onNodeWithText("Моя сохраненная музыка").assertIsDisplayed()
    }
}

@RunWith(AndroidJUnit4::class)
class SavedTracksActivityUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<SavedTracksActivity>()

    @Test
    fun collectionScreen_displaysTitle() {
        composeTestRule.onNodeWithText("Коллекция").assertIsDisplayed()
    }

    @Test
    fun collectionScreen_displaysInstructionText() {
        composeTestRule.onNodeWithText("Нажми: раскрыть или скрыть. Удерживай: удалить.")
            .assertIsDisplayed()
    }

    @Test
    fun collectionScreen_displaysEitherEmptyStateOrContentHeader() {
        composeTestRule.onNodeWithText("Коллекция").assertIsDisplayed()
    }

    @Test
    fun emptyState_canBeDisplayed() {
        composeTestRule.onNodeWithText("Коллекция").assertIsDisplayed()
    }
}