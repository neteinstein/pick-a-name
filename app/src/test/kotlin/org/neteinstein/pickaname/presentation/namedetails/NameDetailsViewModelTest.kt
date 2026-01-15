package org.neteinstein.pickaname.presentation.namedetails

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.neteinstein.pickaname.data.repository.NameRepository
import org.neteinstein.pickaname.domain.model.Name

@OptIn(ExperimentalCoroutinesApi::class)
class NameDetailsViewModelTest {
    
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: NameRepository
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: NameDetailsViewModel
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        savedStateHandle = SavedStateHandle()
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initial state is Loading`() = runTest {
        // Given
        savedStateHandle["nameId"] = 1L
        coEvery { repository.getNameById(1L) } returns Name(1, "João", Name.Gender.MALE, "")
        
        // When
        viewModel = NameDetailsViewModel(repository, savedStateHandle)
        
        // Then - initial state before coroutine execution
        assertThat(viewModel.uiState.value).isInstanceOf(NameDetailsUiState.Loading::class.java)
    }
    
    @Test
    fun `loads name successfully`() = runTest {
        // Given
        val name = Name(1, "João", Name.Gender.MALE, "Portuguese name")
        savedStateHandle["nameId"] = 1L
        coEvery { repository.getNameById(1L) } returns name
        
        // When
        viewModel = NameDetailsViewModel(repository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(NameDetailsUiState.Success::class.java)
        val successState = state as NameDetailsUiState.Success
        assertThat(successState.name.id).isEqualTo(1L)
        assertThat(successState.name.name).isEqualTo("João")
        assertThat(successState.name.gender).isEqualTo(Name.Gender.MALE)
    }
    
    @Test
    fun `shows error when name not found`() = runTest {
        // Given
        savedStateHandle["nameId"] = 999L
        coEvery { repository.getNameById(999L) } returns null
        
        // When
        viewModel = NameDetailsViewModel(repository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(NameDetailsUiState.Error::class.java)
        val errorState = state as NameDetailsUiState.Error
        assertThat(errorState.message).isEqualTo("Name not found")
    }
    
    @Test
    fun `shows error for invalid name id`() = runTest {
        // Given - no nameId in savedStateHandle (defaults to -1)
        
        // When
        viewModel = NameDetailsViewModel(repository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(NameDetailsUiState.Error::class.java)
        val errorState = state as NameDetailsUiState.Error
        assertThat(errorState.message).isEqualTo("Invalid name ID")
    }
    
    @Test
    fun `handles repository exception`() = runTest {
        // Given
        savedStateHandle["nameId"] = 1L
        coEvery { repository.getNameById(1L) } throws Exception("Database error")
        
        // When
        viewModel = NameDetailsViewModel(repository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(NameDetailsUiState.Error::class.java)
        val errorState = state as NameDetailsUiState.Error
        assertThat(errorState.message).isEqualTo("Database error")
    }
}
