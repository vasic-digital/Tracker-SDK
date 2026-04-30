package lava.sdk.testing

import lava.sdk.api.HealthState
import lava.sdk.api.MirrorUrl
import lava.sdk.mirror.HealthProbe

/**
 * Test [HealthProbe] that returns the configured [HealthState] per mirror URL,
 * defaulting to [HealthState.HEALTHY] when no override is set.
 *
 * Use [setState] from a test to flip a mirror to UNHEALTHY/DEGRADED and confirm
 * downstream behaviour (fallback selection, alerting, etc.) reacts as designed.
 */
class FakeHealthProbe(
    initial: Map<String, HealthState> = emptyMap(),
    private val default: HealthState = HealthState.HEALTHY,
) : HealthProbe {
    private val states: MutableMap<String, HealthState> = initial.toMutableMap()

    fun setState(url: String, state: HealthState) {
        states[url] = state
    }

    override suspend fun probe(endpoint: MirrorUrl): HealthState =
        states[endpoint.url] ?: default
}
