package lava.sdk.mirror

import kotlinx.coroutines.flow.Flow
import lava.sdk.api.MirrorState
import lava.sdk.api.MirrorUrl

/**
 * Coordinates a set of mirror endpoints for a [MirrorGroup]:
 *
 * - tracks per-mirror health (HEALTHY / DEGRADED / UNHEALTHY / UNKNOWN)
 * - exposes a fallback executor that orders by (health, priority) and
 *   skips UNHEALTHY mirrors automatically
 * - records success/failure to drive state transitions per
 *   [lava.sdk.api.FallbackPolicy]
 */
interface MirrorManager {

    /** Returns the highest-priority mirror with health HEALTHY or DEGRADED, or null if none. */
    suspend fun getHealthyMirror(groupId: String): MirrorUrl?

    /**
     * Executes [op] against mirrors in priority order, skipping UNHEALTHY ones,
     * recording success/failure, and falling back on per-mirror failure.
     * Throws [lava.sdk.api.MirrorUnavailableException] if all attempts fail.
     */
    suspend fun <T> executeWithFallback(
        groupId: String,
        op: suspend (MirrorUrl) -> T,
    ): T

    /** Stream of current state for the group's mirrors. */
    fun observeHealth(groupId: String): Flow<List<MirrorState>>

    /** Probe all registered mirrors for the group right now (out-of-band). */
    suspend fun probeAll(groupId: String)

    /** Mark a successful op against [endpoint] (clears consecutiveFailures, ensures HEALTHY/DEGRADED). */
    suspend fun reportSuccess(endpoint: MirrorUrl)

    /** Mark a failure against [endpoint] (increments counter, may transition state). */
    suspend fun reportFailure(endpoint: MirrorUrl, cause: Throwable)
}
