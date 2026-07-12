package org.neteinstein.pickaname.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

/**
 * Downloads the raw bytes of the names-list PDF.
 *
 * The IRN server returns a 502 for requests without a browser-like `User-Agent` header (observed
 * in practice), so we always send one.
 */
class NameListRemoteDataSource(
    private val httpClient: OkHttpClient
) {
    suspend fun downloadPdf(url: String): ByteArray = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", BROWSER_USER_AGENT)
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Unexpected response ${response.code} while downloading $url")
            }
            // response.body is non-null in OkHttp 5.x; guard against a genuinely empty payload.
            val bytes = response.body.bytes()
            if (bytes.isEmpty()) {
                throw IOException("Empty response body from $url")
            }
            bytes
        }
    }

    private companion object {
        const val BROWSER_USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
    }
}
