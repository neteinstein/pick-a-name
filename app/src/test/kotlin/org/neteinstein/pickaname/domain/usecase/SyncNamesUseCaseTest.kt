package org.neteinstein.pickaname.domain.usecase

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.neteinstein.pickaname.domain.model.SyncFailureReason
import org.neteinstein.pickaname.domain.model.SyncOutcome
import org.neteinstein.pickaname.domain.repository.NameSyncRepository

class SyncNamesUseCaseTest {

    private val nameSyncRepository: NameSyncRepository = mockk()
    private val useCase = SyncNamesUseCase(nameSyncRepository)
    private val url = "https://example.com/list.pdf"

    @Test
    fun `delegates to the sync repository and returns its success outcome`() = runTest {
        coEvery { nameSyncRepository.syncFromUrl(url) } returns SyncOutcome.Success(namesLoaded = 42)

        val result = useCase(url)

        assertThat(result).isEqualTo(SyncOutcome.Success(namesLoaded = 42))
        coVerify(exactly = 1) { nameSyncRepository.syncFromUrl(url) }
    }

    @Test
    fun `returns the sync repository's error outcome unchanged`() = runTest {
        coEvery { nameSyncRepository.syncFromUrl(url) } returns
            SyncOutcome.Error(SyncFailureReason.NETWORK, "boom")

        val result = useCase(url)

        assertThat(result).isEqualTo(SyncOutcome.Error(SyncFailureReason.NETWORK, "boom"))
    }
}
