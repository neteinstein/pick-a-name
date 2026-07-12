package org.neteinstein.pickaname.data.repository

import org.neteinstein.pickaname.data.local.database.NameDao
import org.neteinstein.pickaname.data.mapper.toEntity
import org.neteinstein.pickaname.data.parser.NameListTextParser
import org.neteinstein.pickaname.data.parser.PdfTextExtractor
import org.neteinstein.pickaname.data.remote.NameListRemoteDataSource
import org.neteinstein.pickaname.domain.model.SyncFailureReason
import org.neteinstein.pickaname.domain.model.SyncOutcome
import org.neteinstein.pickaname.domain.repository.NameSyncRepository
import java.io.IOException

class NameSyncRepositoryImpl(
    private val remoteDataSource: NameListRemoteDataSource,
    private val pdfTextExtractor: PdfTextExtractor,
    private val textParser: NameListTextParser,
    private val nameDao: NameDao
) : NameSyncRepository {

    override suspend fun syncFromUrl(url: String): SyncOutcome {
        val pdfBytes = try {
            remoteDataSource.downloadPdf(url)
        } catch (e: IOException) {
            return SyncOutcome.Error(SyncFailureReason.NETWORK, e.message)
        } catch (e: IllegalArgumentException) {
            return SyncOutcome.Error(SyncFailureReason.INVALID_SOURCE, e.message)
        }

        return try {
            val rawText = pdfTextExtractor.extractText(pdfBytes)
            val parsedNames = textParser.parse(rawText).distinct()
            if (parsedNames.isEmpty()) {
                SyncOutcome.Error(SyncFailureReason.NO_NAMES_FOUND)
            } else {
                val entities = parsedNames.map { it.toEntity() }
                nameDao.replaceAll(entities)
                SyncOutcome.Success(namesLoaded = entities.size)
            }
        } catch (e: Exception) {
            // Anything from a corrupt PDF, an unexpected document layout, or a DB failure is
            // surfaced as a recoverable sync error rather than crashing the app.
            SyncOutcome.Error(SyncFailureReason.INVALID_SOURCE, e.message)
        }
    }
}
