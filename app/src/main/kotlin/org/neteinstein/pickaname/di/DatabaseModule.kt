package org.neteinstein.pickaname.di

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.neteinstein.pickaname.data.local.database.AppDatabase

/**
 * Room database + DAO providers.
 */
val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }
    single { get<AppDatabase>().nameDao() }
}
