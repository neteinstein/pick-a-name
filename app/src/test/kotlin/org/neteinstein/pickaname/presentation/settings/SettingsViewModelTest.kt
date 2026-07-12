package org.neteinstein.pickaname.presentation.settings

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.neteinstein.pickaname.domain.model.NamesSourceDefaults
import org.neteinstein.pickaname.domain.usecase.GetSourceUrlUseCase
import org.neteinstein.pickaname.domain.usecase.ResetSourceUrlUseCase
import org.neteinstein.pickaname.domain.usecase.UpdateSourceUrlUseCase
import org.neteinstein.pickaname.util.MainDispatcherRule

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getSourceUrlUseCase: GetSourceUrlUseCase = mockk()
    private val updateSourceUrlUseCase: UpdateSourceUrlUseCase = mockk()
    private val resetSourceUrlUseCase: ResetSourceUrlUseCase = mockk()

    private fun createViewModel(): SettingsViewModel {
        coEvery { getSourceUrlUseCase() } returns "https://current.example.com/list.pdf"
        return SettingsViewModel(getSourceUrlUseCase, updateSourceUrlUseCase, resetSourceUrlUseCase)
    }

    @Test
    fun `loads the currently configured url on init`() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = createViewModel()
        runCurrent()

        assertThat(viewModel.uiState.value.sourceUrl).isEqualTo("https://current.example.com/list.pdf")
        assertThat(viewModel.uiState.value.urlError).isFalse()
    }

    @Test
    fun `saving a valid url clears any error and emits a SourceUpdated event`() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = createViewModel()
        runCurrent()
        coEvery { updateSourceUrlUseCase(any()) } returns Result.success(Unit)

        viewModel.events.test {
            viewModel.onUrlChange("https://new.example.com/list.pdf")
            viewModel.onSave()
            runCurrent()

            assertThat(awaitItem()).isEqualTo(SettingsEvent.SourceUpdated)
        }
        assertThat(viewModel.uiState.value.urlError).isFalse()
        coVerify(exactly = 1) { updateSourceUrlUseCase("https://new.example.com/list.pdf") }
    }

    @Test
    fun `saving an invalid url surfaces an error and does not emit an event`() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = createViewModel()
        runCurrent()
        coEvery { updateSourceUrlUseCase(any()) } returns Result.failure(IllegalArgumentException("bad url"))

        viewModel.events.test {
            viewModel.onUrlChange("not a url")
            viewModel.onSave()
            runCurrent()

            expectNoEvents()
        }
        assertThat(viewModel.uiState.value.urlError).isTrue()
    }

    @Test
    fun `changing the url clears a previous error`() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = createViewModel()
        runCurrent()
        coEvery { updateSourceUrlUseCase(any()) } returns Result.failure(IllegalArgumentException("bad url"))
        viewModel.onUrlChange("not a url")
        viewModel.onSave()
        runCurrent()
        assertThat(viewModel.uiState.value.urlError).isTrue()

        viewModel.onUrlChange("https://fixed.example.com/list.pdf")

        assertThat(viewModel.uiState.value.urlError).isFalse()
    }

    @Test
    fun `resetting restores the default url and emits a SourceUpdated event`() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = createViewModel()
        runCurrent()
        coEvery { resetSourceUrlUseCase() } returns Unit

        viewModel.events.test {
            viewModel.onReset()
            runCurrent()

            assertThat(awaitItem()).isEqualTo(SettingsEvent.SourceUpdated)
        }
        assertThat(viewModel.uiState.value.sourceUrl).isEqualTo(NamesSourceDefaults.DEFAULT_SOURCE_URL)
        coVerify(exactly = 1) { resetSourceUrlUseCase() }
    }
}
