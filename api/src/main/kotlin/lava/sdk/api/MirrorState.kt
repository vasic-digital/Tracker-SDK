package lava.sdk.api

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class MirrorState(
    val mirror: MirrorUrl,
    val health: HealthState,
    val lastCheck: Instant?,
    val consecutiveFailures: Int,
)
