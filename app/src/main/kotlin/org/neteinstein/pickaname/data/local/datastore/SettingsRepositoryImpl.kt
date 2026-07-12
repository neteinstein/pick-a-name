package org.neteinstein.pickaname.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.neteinstein.pickaname.domain.model.NamesSourceDefaults
import org.neteinstein.pickaname.domain.repository.SettingsRepository

/**
 * [SettingsRepository] backed by Preferences DataStore, so the configured source URL survives
 * process death and app restarts.
 */
class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    override fun observeSourceUrl(): Flow<String> =
        dataStore.data.map { prefs ->
            prefs[SOURCE_URL_KEY] ?: NamesSourceDefaults.DEFAULT_SOURCE_URL
        }

    override suspend fun getSourceUrl(): String = observeSourceUrl().first()

    override suspend fun setSourceUrl(url: String) {
        dataStore.edit { prefs -> prefs[SOURCE_URL_KEY] = url }
    }

    override suspend fun resetSourceUrlToDefault() {
        dataStore.edit { prefs -> prefs[SOURCE_URL_KEY] = NamesSourceDefaults.DEFAULT_SOURCE_URL }
    }

    private companion object {
        val SOURCE_URL_KEY = stringPreferencesKey("names_source_url")
    }
}
