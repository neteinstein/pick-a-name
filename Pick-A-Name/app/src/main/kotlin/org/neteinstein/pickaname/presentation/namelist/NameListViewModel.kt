package org.neteinstein.pickaname.presentation.namelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.neteinstein.pickaname.data.repository.NameRepository
import org.neteinstein.pickaname.domain.model.Name
import javax.inject.Inject

/**
 * ViewModel for the name list screen
 */
@HiltViewModel
class NameListViewModel @Inject constructor(
    private val nameRepository: NameRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<NameListUiState>(NameListUiState.Loading)
    val uiState: StateFlow<NameListUiState> = _uiState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    init {
        loadNames()
    }
    
    private fun loadNames() {
        viewModelScope.launch {
            combine(
                nameRepository.getAllowedNames(),
                _searchQuery
            ) { names, query ->
                if (query.isBlank()) {
                    names
                } else {
                    names.filter { it.name.contains(query, ignoreCase = true) }
                }
            }
                .catch { exception ->
                    _uiState.value = NameListUiState.Error(exception.message ?: "Unknown error")
                }
                .collect { names ->
                    _uiState.value = if (names.isEmpty()) {
                        NameListUiState.Empty
                    } else {
                        NameListUiState.Success(names)
                    }
                }
        }
    }
    
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
    
    fun onNameClicked(nameId: Long) {
        // Navigation will be handled by the composable
    }
}

/**
 * UI state for name list screen
 */
sealed interface NameListUiState {
    object Loading : NameListUiState
    object Empty : NameListUiState
    data class Success(val names: List<Name>) : NameListUiState
    data class Error(val message: String) : NameListUiState
}
