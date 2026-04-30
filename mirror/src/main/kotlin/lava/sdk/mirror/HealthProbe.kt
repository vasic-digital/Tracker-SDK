package lava.sdk.mirror

import lava.sdk.api.HealthState
import lava.sdk.api.MirrorUrl

/**
 * Probes a mirror endpoint to determine its current [HealthState].
 *
 * Implementations decide what "healthy" means for the underlying transport
 * (HTTP body marker, HTTP3 ping, custom protocol handshake, etc.).
 */
interface HealthProbe {
    suspend fun probe(endpoint: MirrorUrl): HealthState
}
