package org.neteinstein.pickaname.presentation.namelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import org.neteinstein.pickaname.domain.model.Gender
import org.neteinstein.pickaname.domain.model.NameEntry
import org.neteinstein.pickaname.domain.model.NameFilter
import org.neteinstein.pickaname.domain.usecase.ObserveNameCountUseCase
import org.neteinstein.pickaname.domain.usecase.ObserveNamesUseCase

data class NameListUiState(
    val names: List<NameEntry> = emptyList(),
    val count: Int = 0,
    val query: String = "",
    val selectedGender: Gender? = null,
    val selectedInitial: Char? = null
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class NameListViewModel(
    private val observeNamesUseCase: ObserveNamesUseCase,
    private val observeNameCountUseCase: ObserveNameCountUseCase
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val gender = MutableStateFlow<Gender?>(null)
    private val initial = MutableStateFlow<Char?>(null)

    /**
     * [query]'s very first value passes through immediately; only later edits are debounced.
     * Without this split, `combine` below would wait out the full debounce window before
     * producing *any* result, delaying gender/initial selections made right as the screen opens.
     */
    private val debouncedQuery: Flow<String> = merge(query.take(1), query.drop(1).debounce(250))

    /**
     * Deliberately a plain [Flow], not a [StateFlow]. An intermediate `stateIn(Eagerly, ...)`
     * here would run as its own independent collector, racing against [uiState]'s subscription:
     * `uiState` could see a stale/default filter snapshot before this producer's first real
     * value lands, emitting one incorrect transient result. Keeping it as a plain flow means
     * [uiState]'s own subscription is what drives this combine, so there is exactly one
     * producer and no stale-snapshot race.
     */
    private val filter: Flow<NameFilter> =
        combine(debouncedQuery, gender, initial) { q, g, i ->
            NameFilter(query = q, gender = g, initial = i)
        }

    val uiState: StateFlow<NameListUiState> = filter.flatMapLatest { currentFilter ->
        combine(
            observeNamesUseCase(currentFilter),
            observeNameCountUseCase(currentFilter)
        ) { names, count ->
            NameListUiState(
                names = names,
                count = count,
                query = currentFilter.query,
                selectedGender = currentFilter.gender,
                selectedInitial = currentFilter.initial
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NameListUiState())

    fun onQueryChange(newQuery: String) {
        query.value = newQuery
    }

    fun onGenderSelected(selected: Gender?) {
        gender.value = selected
    }

    fun onInitialSelected(selected: Char?) {
        initial.value = selected
    }
}
