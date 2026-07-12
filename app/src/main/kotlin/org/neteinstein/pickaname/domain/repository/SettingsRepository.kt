package org.neteinstein.pickaname.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Persisted user preferences: currently just the URL the names list PDF is downloaded from.
 */
interface SettingsRepository {

    /** Emits the currently configured source URL, defaulting to [org.neteinstein.pickaname.domain.model.NamesSourceDefaults.DEFAULT_SOURCE_URL]. */
    fun observeSourceUrl(): Flow<String>

    suspend fun getSourceUrl(): String

    suspend fun setSourceUrl(url: String)

    suspend fun resetSourceUrlToDefault()
}
