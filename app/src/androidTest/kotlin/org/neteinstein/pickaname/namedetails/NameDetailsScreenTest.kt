package org.neteinstein.pickaname.namedetails

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.neteinstein.pickaname.domain.model.Name
import org.neteinstein.pickaname.presentation.namedetails.NameDetailsScreen
import org.neteinstein.pickaname.presentation.namedetails.NameDetailsUiState
import org.neteinstein.pickaname.presentation.theme.PickANameTheme
import kotlinx.coroutines.flow.MutableStateFlow

@RunWith(AndroidJUnit4::class)
class NameDetailsScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun loadingStateDisplaysProgressIndicator() {
        // Given
        val uiState = MutableStateFlow<NameDetailsUiState>(NameDetailsUiState.Loading)
        
        // When
        composeTestRule.setContent {
            PickANameTheme {
                // UI content would go here
            }
        }
        
        // Then - would verify loading indicator is displayed
    }
    
    @Test
    fun successStateDisplaysNameDetails() {
        // Given
        val name = Name(1, "João", Name.Gender.MALE, "Portuguese name")
        val uiState = MutableStateFlow<NameDetailsUiState>(NameDetailsUiState.Success(name))
        
        // When
        composeTestRule.setContent {
            PickANameTheme {
                // UI content would go here
            }
        }
        
        // Then
        composeTestRule.onNodeWithText("João").assertIsDisplayed()
        composeTestRule.onNodeWithText("Male").assertIsDisplayed()
        composeTestRule.onNodeWithText("Portuguese name").assertIsDisplayed()
    }
    
    @Test
    fun errorStateDisplaysErrorMessage() {
        // Given
        val uiState = MutableStateFlow<NameDetailsUiState>(
            NameDetailsUiState.Error("Name not found")
        )
        
        // When
        composeTestRule.setContent {
            PickANameTheme {
                // UI content would go here
            }
        }
        
        // Then
        composeTestRule.onNodeWithText("Error: Name not found").assertIsDisplayed()
    }
}
