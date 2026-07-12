package org.neteinstein.pickaname.di

import org.koin.dsl.module
import org.neteinstein.pickaname.data.local.datastore.SettingsRepositoryImpl
import org.neteinstein.pickaname.data.parser.NameListTextParser
import org.neteinstein.pickaname.data.parser.PdfTextExtractor
import org.neteinstein.pickaname.data.remote.NameListRemoteDataSource
import org.neteinstein.pickaname.data.repository.NameRepositoryImpl
import org.neteinstein.pickaname.data.repository.NameSyncRepositoryImpl
import org.neteinstein.pickaname.domain.repository.NameRepository
import org.neteinstein.pickaname.domain.repository.NameSyncRepository
import org.neteinstein.pickaname.domain.repository.SettingsRepository

/**
 * Binds domain repository interfaces to their data-layer implementations, plus the small
 * data-layer collaborators (remote download, PDF text extraction, text parsing) they depend on.
 */
val repositoryModule = module {
    single { NameListRemoteDataSource(get()) }
    single { PdfTextExtractor() }
    single { NameListTextParser() }

    single<NameRepository> { NameRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<NameSyncRepository> { NameSyncRepositoryImpl(get(), get(), get(), get()) }
}
