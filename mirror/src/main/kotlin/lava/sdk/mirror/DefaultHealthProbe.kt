package lava.sdk.mirror

import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import lava.sdk.api.HealthState
import lava.sdk.api.MirrorUrl
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * OkHttp-backed probe that classifies mirror health by:
 *
 * - HTTP status (non-2xx → UNHEALTHY)
 * - body marker presence (a 200 OK that does not contain [expectedMarker]
 *   is treated as a captive-portal-style fake response → UNHEALTHY)
 * - elapsed latency thresholds:
 *   - `>= unhealthyThresholdMs` → UNHEALTHY
 *   - `>= degradedThresholdMs` → DEGRADED
 *   - else HEALTHY
 *
 * Any thrown error or [timeout] expiry maps to UNHEALTHY.
 */
class DefaultHealthProbe(
    private val expectedMarker: String,
    private val timeout: Duration,
    private val degradedThresholdMs: Long = 5_000L,
    private val unhealthyThresholdMs: Long = 10_000L,
    private val client: OkHttpClient = defaultClient(timeout),
) : HealthProbe {

    override suspend fun probe(endpoint: MirrorUrl): HealthState = withContext(Dispatchers.IO) {
        val started = System.currentTimeMillis()
        val request = Request.Builder().url(endpoint.url).get().build()
        val outcome = withTimeoutOrNull(timeout) {
            runCatching { client.newCall(request).execute() }
        } ?: return@withContext HealthState.UNHEALTHY // timed out

        outcome.fold(
            onSuccess = { response ->
                response.use { r ->
                    val elapsed = System.currentTimeMillis() - started
                    val body = r.body?.string().orEmpty()
                    when {
                        !r.isSuccessful -> HealthState.UNHEALTHY
                        !body.contains(expectedMarker, ignoreCase = true) -> HealthState.UNHEALTHY
                        elapsed >= unhealthyThresholdMs -> HealthState.UNHEALTHY
                        elapsed >= degradedThresholdMs -> HealthState.DEGRADED
                        else -> HealthState.HEALTHY
                    }
                }
            },
            onFailure = { HealthState.UNHEALTHY },
        )
    }

    private companion object {
        fun defaultClient(timeout: Duration): OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(timeout.inWholeMilliseconds, TimeUnit.MILLISECONDS)
            .readTimeout(timeout.inWholeMilliseconds, TimeUnit.MILLISECONDS)
            .build()
    }
}
