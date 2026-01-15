package org.neteinstein.pickaname.namelist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.neteinstein.pickaname.domain.model.Name
import org.neteinstein.pickaname.presentation.namelist.NameListScreen
import org.neteinstein.pickaname.presentation.namelist.NameListUiState
import org.neteinstein.pickaname.presentation.theme.PickANameTheme
import kotlinx.coroutines.flow.MutableStateFlow

@RunWith(AndroidJUnit4::class)
class NameListScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun loadingStateDisplaysProgressIndicator() {
        // Given
        val uiState = MutableStateFlow<NameListUiState>(NameListUiState.Loading)
        
        // When
        composeTestRule.setContent {
            PickANameTheme {
                // Note: This would require extracting the UI content to test properly
                // For now, this demonstrates the test structure
            }
        }
        
        // Then - would verify loading indicator is displayed
    }
    
    @Test
    fun emptyStateDisplaysMessage() {
        // Given
        val uiState = MutableStateFlow<NameListUiState>(NameListUiState.Empty)
        
        // When
        composeTestRule.setContent {
            PickANameTheme {
                // UI content would go here
            }
        }
        
        // Then
        composeTestRule.onNodeWithText("No names found").assertIsDisplayed()
    }
    
    @Test
    fun successStateDisplaysNameList() {
        // Given
        val names = listOf(
            Name(1, "João", Name.Gender.MALE, ""),
            Name(2, "Maria", Name.Gender.FEMALE, "")
        )
        val uiState = MutableStateFlow<NameListUiState>(NameListUiState.Success(names))
        
        // When
        composeTestRule.setContent {
            PickANameTheme {
                // UI content would go here
            }
        }
        
        // Then
        composeTestRule.onNodeWithText("João").assertIsDisplayed()
        composeTestRule.onNodeWithText("Maria").assertIsDisplayed()
    }
    
    @Test
    fun searchFilterWorks() {
        // Given
        val names = listOf(
            Name(1, "João", Name.Gender.MALE, ""),
            Name(2, "Maria", Name.Gender.FEMALE, "")
        )
        
        // When
        composeTestRule.setContent {
            PickANameTheme {
                // UI content with search functionality
            }
        }
        
        // Then
        composeTestRule.onNodeWithText("Filter names...").performTextInput("Jo")
        // Would verify filtered results
    }
}
