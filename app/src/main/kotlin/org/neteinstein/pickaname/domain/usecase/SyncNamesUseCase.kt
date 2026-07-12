package org.neteinstein.pickaname.domain.usecase

import org.neteinstein.pickaname.domain.model.SyncOutcome
import org.neteinstein.pickaname.domain.repository.NameSyncRepository

/**
 * Downloads the names list from the given URL, parses it, and purges + repopulates the local
 * database. Used both for the first-run load and whenever the user changes the source URL.
 */
class SyncNamesUseCase(private val nameSyncRepository: NameSyncRepository) {
    suspend operator fun invoke(url: String): SyncOutcome = nameSyncRepository.syncFromUrl(url)
}
