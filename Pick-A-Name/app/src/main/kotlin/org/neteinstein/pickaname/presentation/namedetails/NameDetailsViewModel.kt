package org.neteinstein.pickaname.presentation.namedetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.neteinstein.pickaname.data.repository.NameRepository
import org.neteinstein.pickaname.domain.model.Name
import javax.inject.Inject

/**
 * ViewModel for the name details screen
 */
@HiltViewModel
class NameDetailsViewModel @Inject constructor(
    private val nameRepository: NameRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val nameId: Long = savedStateHandle.get<Long>("nameId") ?: -1
    
    private val _uiState = MutableStateFlow<NameDetailsUiState>(NameDetailsUiState.Loading)
    val uiState: StateFlow<NameDetailsUiState> = _uiState.asStateFlow()
    
    init {
        loadNameDetails()
    }
    
    private fun loadNameDetails() {
        if (nameId == -1L) {
            _uiState.value = NameDetailsUiState.Error("Invalid name ID")
            return
        }
        
        viewModelScope.launch {
            try {
                val name = nameRepository.getNameById(nameId)
                _uiState.value = if (name != null) {
                    NameDetailsUiState.Success(name)
                } else {
                    NameDetailsUiState.Error("Name not found")
                }
            } catch (exception: Exception) {
                _uiState.value = NameDetailsUiState.Error(
                    exception.message ?: "Unknown error"
                )
            }
        }
    }
}

/**
 * UI state for name details screen
 */
sealed interface NameDetailsUiState {
    object Loading : NameDetailsUiState
    data class Success(val name: Name) : NameDetailsUiState
    data class Error(val message: String) : NameDetailsUiState
}
