package org.neteinstein.pickaname

import android.app.Application
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.neteinstein.pickaname.di.appModule
import org.neteinstein.pickaname.di.databaseModule
import org.neteinstein.pickaname.di.dataStoreModule
import org.neteinstein.pickaname.di.repositoryModule
import org.neteinstein.pickaname.di.useCaseModule
import org.neteinstein.pickaname.di.viewModelModule

class PickANameApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Required once before any PdfTextExtractor usage - lets pdfbox-android load its
        // bundled font/glyph resources from assets.
        PDFBoxResourceLoader.init(applicationContext)

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@PickANameApplication)
            modules(
                appModule,
                databaseModule,
                dataStoreModule,
                repositoryModule,
                useCaseModule,
                viewModelModule
            )
        }
    }
}
