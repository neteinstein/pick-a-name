package org.neteinstein.pickaname.data.remote

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException

class NameListRemoteDataSourceTest {

    private val server = MockWebServer()
    private val dataSource = NameListRemoteDataSource(OkHttpClient())

    @Before
    fun setUp() {
        server.start()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `returns the response body bytes on a successful download`() = runTest {
        val bytes = byteArrayOf(1, 2, 3, 4)
        server.enqueue(MockResponse().setResponseCode(200).setBody(String(bytes, Charsets.ISO_8859_1)))

        val result = dataSource.downloadPdf(server.url("/list.pdf").toString())

        assertThat(result).isEqualTo(bytes)
    }

    @Test
    fun `sends a browser-like User-Agent header`() = runTest {
        server.enqueue(MockResponse().setResponseCode(200).setBody("pdf-bytes"))

        dataSource.downloadPdf(server.url("/list.pdf").toString())

        val recordedRequest = server.takeRequest()
        assertThat(recordedRequest.getHeader("User-Agent")).contains("Mozilla")
    }

    @Test
    fun `throws IOException on a non-2xx response`() = runTest {
        server.enqueue(MockResponse().setResponseCode(502))

        val exception = runCatching {
            dataSource.downloadPdf(server.url("/list.pdf").toString())
        }.exceptionOrNull()

        assertThat(exception).isInstanceOf(IOException::class.java)
    }

    @Test
    fun `throws IOException on an empty response body`() = runTest {
        server.enqueue(MockResponse().setResponseCode(200).setBody(""))

        val exception = runCatching {
            dataSource.downloadPdf(server.url("/list.pdf").toString())
        }.exceptionOrNull()

        assertThat(exception).isInstanceOf(IOException::class.java)
    }
}
