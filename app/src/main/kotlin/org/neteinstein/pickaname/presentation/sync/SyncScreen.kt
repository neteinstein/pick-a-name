package org.neteinstein.pickaname.presentation.sync

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.neteinstein.pickaname.R
import org.neteinstein.pickaname.domain.model.SyncFailureReason

/**
 * Shown on first run and whenever the names source URL changes. Downloads, parses and persists
 * the names list, showing a loading message while it works and a retry/edit-source recovery UI
 * on failure (the default source is a real government server observed to be flaky).
 */
@Composable
fun SyncScreen(
    onSyncSuccess: () -> Unit,
    onEditSource: () -> Unit,
    viewModel: SyncViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is SyncUiState.Success) {
            onSyncSuccess()
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is SyncUiState.Loading, is SyncUiState.Success -> LoadingContent()
            is SyncUiState.Error -> ErrorContent(
                reason = state.reason,
                onRetry = viewModel::retry,
                onEditSource = onEditSource
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.sync_loading_title),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.sync_loading_message),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorContent(
    reason: SyncFailureReason,
    onRetry: () -> Unit,
    onEditSource: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.height(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.sync_error_title),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(reason.messageRes()),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedButton(
                onClick = onEditSource,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.sync_edit_source))
            }
            Button(
                onClick = onRetry,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.sync_retry))
            }
        }
    }
}

private fun SyncFailureReason.messageRes(): Int = when (this) {
    SyncFailureReason.NETWORK -> R.string.sync_error_network
    SyncFailureReason.INVALID_SOURCE -> R.string.sync_error_invalid_source
    SyncFailureReason.NO_NAMES_FOUND -> R.string.sync_error_no_names_found
    SyncFailureReason.UNKNOWN -> R.string.sync_error_unknown
}
