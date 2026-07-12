package org.neteinstein.pickaname.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val SETTINGS_DATASTORE_FILE_NAME = "pick_a_name_settings.preferences_pb"

/**
 * Preferences DataStore instance used to persist the configurable names-source URL.
 */
val dataStoreModule = module {
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.create(
            produceFile = {
                androidContext().preferencesDataStoreFile(SETTINGS_DATASTORE_FILE_NAME)
            }
        )
    }
}
