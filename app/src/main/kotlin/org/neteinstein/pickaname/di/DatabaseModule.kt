package org.neteinstein.pickaname.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.neteinstein.pickaname.data.database.NameDao
import org.neteinstein.pickaname.data.database.NameDatabase
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Hilt module for providing database-related dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class ApplicationScope
    
    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob())
    }
    
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        @ApplicationScope scope: CoroutineScope
    ): NameDatabase {
        return NameDatabase.getDatabase(context, scope)
    }
    
    @Provides
    @Singleton
    fun provideNameDao(database: NameDatabase): NameDao {
        return database.nameDao()
    }
}
