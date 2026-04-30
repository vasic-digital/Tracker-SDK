package lava.sdk.api

import kotlinx.serialization.Serializable

@Serializable
enum class HealthState { HEALTHY, DEGRADED, UNHEALTHY, UNKNOWN }
