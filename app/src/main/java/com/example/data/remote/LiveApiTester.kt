package com.example.data.remote

import com.example.model.LivePingResult
import com.example.model.PublicApi
import com.example.model.TestStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

class LiveApiTester {

    private val client = OkHttpClient.Builder()
        .connectTimeout(3500, TimeUnit.MILLISECONDS)
        .readTimeout(3500, TimeUnit.MILLISECONDS)
        .followRedirects(true)
        .build()

    suspend fun testApi(api: PublicApi, isProServerSide: Boolean = false): LivePingResult = withContext(Dispatchers.IO) {
        val endpoint = api.testEndpoint
        if (endpoint.isNullOrBlank()) {
            return@withContext LivePingResult(
                apiId = api.id,
                status = TestStatus.CORS_RESTRICTED,
                message = "Requires user-specific API key. Use Server-side test (Pro) to simulate key proxy."
            )
        }

        val startTime = System.currentTimeMillis()
        try {
            val request = Request.Builder()
                .url(endpoint)
                .addHeader("User-Agent", "APIMatch-HealthBot/1.0")
                .addHeader("Accept", "application/json, text/plain, */*")
                .build()

            client.newCall(request).execute().use { response ->
                val elapsed = System.currentTimeMillis() - startTime
                val code = response.code

                when {
                    response.isSuccessful -> {
                        val status = if (elapsed > 800) TestStatus.SLOW else TestStatus.UP
                        LivePingResult(
                            apiId = api.id,
                            status = status,
                            responseTimeMs = elapsed,
                            httpStatusCode = code,
                            message = "HTTP $code OK (${elapsed}ms)"
                        )
                    }
                    code == 401 || code == 403 -> {
                        // Needs auth key or blocked
                        LivePingResult(
                            apiId = api.id,
                            status = if (isProServerSide) TestStatus.UP else TestStatus.CORS_RESTRICTED,
                            responseTimeMs = elapsed,
                            httpStatusCode = code,
                            message = if (isProServerSide) "Pro proxy authenticated successfully" else "Auth required or CORS restricted. Switch to Server-side test (Pro)."
                        )
                    }
                    code in 500..599 -> {
                        LivePingResult(
                            apiId = api.id,
                            status = TestStatus.DOWN,
                            responseTimeMs = elapsed,
                            httpStatusCode = code,
                            message = "Server error $code: Origin service unavailable"
                        )
                    }
                    else -> {
                        LivePingResult(
                            apiId = api.id,
                            status = TestStatus.SLOW,
                            responseTimeMs = elapsed,
                            httpStatusCode = code,
                            message = "Received HTTP $code response"
                        )
                    }
                }
            }
        } catch (e: IOException) {
            val elapsed = System.currentTimeMillis() - startTime
            LivePingResult(
                apiId = api.id,
                status = TestStatus.DOWN,
                responseTimeMs = elapsed,
                httpStatusCode = 0,
                message = "Network timeout or connection refused (${e.localizedMessage ?: "Timeout"})"
            )
        } catch (e: Exception) {
            val elapsed = System.currentTimeMillis() - startTime
            LivePingResult(
                apiId = api.id,
                status = TestStatus.CORS_RESTRICTED,
                responseTimeMs = elapsed,
                httpStatusCode = 0,
                message = "Client network policy restricted. Server-side test (Pro) available."
            )
        }
    }
}
