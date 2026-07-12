package org.neteinstein.pickaname.presentation.namelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.neteinstein.pickaname.R
import org.neteinstein.pickaname.domain.model.Gender
import org.neteinstein.pickaname.domain.model.NameEntry
import org.neteinstein.pickaname.presentation.common.GenderTag

/**
 * Main screen: the full names list with gender/initial filters and a live match count. Reachable
 * only once the database has been populated (splash routes elsewhere otherwise).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameListScreen(
    onOpenSettings: () -> Unit,
    viewModel: NameListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.cd_settings_icon)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = uiState.query,
                onValueChange = viewModel::onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text(stringResource(R.string.name_list_search_hint)) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true
            )

            Text(
                text = stringResource(R.string.filter_section_gender),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            GenderFilterRow(
                selected = uiState.selectedGender,
                onSelected = viewModel::onGenderSelected
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.filter_section_initial),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            InitialFilterRow(
                selected = uiState.selectedInitial,
                onSelected = viewModel::onInitialSelected
            )

            Text(
                text = pluralStringResource(R.plurals.name_count, uiState.count, uiState.count),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (uiState.names.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.name_list_empty_state),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(32.dp)
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.names, key = { it.id }) { entry ->
                        NameRow(entry)
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun GenderFilterRow(selected: Gender?, onSelected: (Gender?) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selected == null,
            onClick = { onSelected(null) },
            label = { Text(stringResource(R.string.filter_gender_all)) }
        )
        FilterChip(
            selected = selected == Gender.FEMALE,
            onClick = { onSelected(Gender.FEMALE) },
            label = { Text(stringResource(R.string.gender_female)) }
        )
        FilterChip(
            selected = selected == Gender.MALE,
            onClick = { onSelected(Gender.MALE) },
            label = { Text(stringResource(R.string.gender_male)) }
        )
    }
}

@Composable
private fun InitialFilterRow(selected: Char?, onSelected: (Char?) -> Unit) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selected == null,
                onClick = { onSelected(null) },
                label = { Text(stringResource(R.string.filter_initial_all)) }
            )
        }
        items(('A'..'Z').toList()) { letter ->
            FilterChip(
                selected = selected == letter,
                onClick = { onSelected(letter) },
                label = { Text(letter.toString()) }
            )
        }
    }
}

@Composable
private fun NameRow(entry: NameEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = entry.name, style = MaterialTheme.typography.bodyLarge)
        GenderTag(gender = entry.gender)
    }
}
