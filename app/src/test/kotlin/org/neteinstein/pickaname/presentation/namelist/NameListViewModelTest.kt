package org.neteinstein.pickaname.presentation.namelist

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
class NameListViewModelTest {
    
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: NameRepository
    private lateinit var viewModel: NameListViewModel
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initial state is Loading`() = runTest {
        // Given
        every { repository.getAllowedNames() } returns flowOf(emptyList())
        
        // When
        viewModel = NameListViewModel(repository)
        
        // Then - initial state before flow collection
        val initialState = viewModel.uiState.value
        assertThat(initialState).isInstanceOf(NameListUiState.Loading::class.java)
    }
    
    @Test
    fun `shows Empty state when no names`() = runTest {
        // Given
        every { repository.getAllowedNames() } returns flowOf(emptyList())
        
        // When
        viewModel = NameListViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(NameListUiState.Empty::class.java)
        }
    }
    
    @Test
    fun `shows Success state with names`() = runTest {
        // Given
        val names = listOf(
            Name(1, "João", Name.Gender.MALE, ""),
            Name(2, "Maria", Name.Gender.FEMALE, "")
        )
        every { repository.getAllowedNames() } returns flowOf(names)
        
        // When
        viewModel = NameListViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(NameListUiState.Success::class.java)
            val successState = state as NameListUiState.Success
            assertThat(successState.names).hasSize(2)
        }
    }
    
    @Test
    fun `filters names based on search query`() = runTest {
        // Given
        val allNames = listOf(
            Name(1, "João", Name.Gender.MALE, ""),
            Name(2, "Maria", Name.Gender.FEMALE, ""),
            Name(3, "José", Name.Gender.MALE, "")
        )
        every { repository.getAllowedNames() } returns flowOf(allNames)
        
        // When
        viewModel = NameListViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onSearchQueryChanged("Jo")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(NameListUiState.Success::class.java)
            val successState = state as NameListUiState.Success
            assertThat(successState.names).hasSize(2) // João and José
        }
    }
    
    @Test
    fun `search query is case insensitive`() = runTest {
        // Given
        val names = listOf(
            Name(1, "João", Name.Gender.MALE, "")
        )
        every { repository.getAllowedNames() } returns flowOf(names)
        
        // When
        viewModel = NameListViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onSearchQueryChanged("joão")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(NameListUiState.Success::class.java)
            val successState = state as NameListUiState.Success
            assertThat(successState.names).hasSize(1)
        }
    }
}
