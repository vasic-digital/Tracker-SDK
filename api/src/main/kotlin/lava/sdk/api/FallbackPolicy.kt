package lava.sdk.api

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class FallbackPolicy(
    val maxAttempts: Int = 3,
    val perAttemptTimeout: Duration = 10.seconds,
    val degradeAfter: Int = 1,
    val unhealthyAfter: Int = 3,
)
