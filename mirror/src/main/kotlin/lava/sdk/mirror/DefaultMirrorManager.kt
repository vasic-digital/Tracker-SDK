package lava.sdk.mirror

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import lava.sdk.api.FallbackPolicy
import lava.sdk.api.HealthState
import lava.sdk.api.MirrorState
import lava.sdk.api.MirrorUnavailableException
import lava.sdk.api.MirrorUrl

/**
 * Default in-memory [MirrorManager] implementation:
 *
 * - holds a [MutableStateFlow] of [MirrorState] per registered [MirrorGroup]
 * - serialises state mutations through a [Mutex] (concurrent reportSuccess /
 *   reportFailure / probeAll calls cannot tear)
 * - orders mirrors by `(healthRank, priority)` and skips UNHEALTHY entries
 *   in [executeWithFallback]
 * - transitions state per the supplied [FallbackPolicy]
 *   (`degradeAfter` consecutive failures → DEGRADED;
 *    `unhealthyAfter` consecutive failures → UNHEALTHY)
 */
class DefaultMirrorManager(
    initialGroups: List<MirrorGroup>,
    private val healthProbe: HealthProbe,
    private val policy: FallbackPolicy = FallbackPolicy(),
    private val clock: Clock = Clock.System,
) : MirrorManager {

    private data class Group(
        val config: MirrorGroup,
        val states: MutableStateFlow<List<MirrorState>>,
    )

    private val groups: Map<String, Group> = initialGroups.associate { g ->
        g.groupId to Group(
            config = g,
            states = MutableStateFlow(
                g.mirrors.map { mirror ->
                    MirrorState(mirror, HealthState.UNKNOWN, lastCheck = null, consecutiveFailures = 0)
                },
            ),
        )
    }

    private val mutex = Mutex()

    override suspend fun getHealthyMirror(groupId: String): MirrorUrl? {
        val g = groups[groupId] ?: return null
        return g.states.value
            .filter { it.health == HealthState.HEALTHY || it.health == HealthState.DEGRADED }
            .sortedWith(compareBy({ healthRank(it.health) }, { it.mirror.priority }))
            .firstOrNull()
            ?.mirror
    }

    override suspend fun <T> executeWithFallback(
        groupId: String,
        op: suspend (MirrorUrl) -> T,
    ): T {
        val g = groups[groupId] ?: throw IllegalArgumentException("Unknown group: $groupId")
        val ordered = g.states.value
            .sortedWith(compareBy({ healthRank(it.health) }, { it.mirror.priority }))
            .filter { it.health != HealthState.UNHEALTHY }
        val tried = mutableListOf<MirrorUrl>()
        var lastCause: Throwable? = null
        for (state in ordered) {
            tried += state.mirror
            try {
                val result = op(state.mirror)
                reportSuccess(state.mirror)
                return result
            } catch (t: Throwable) {
                lastCause = t
                reportFailure(state.mirror, t)
            }
        }
        throw MirrorUnavailableException(tried, lastCause)
    }

    override fun observeHealth(groupId: String): Flow<List<MirrorState>> =
        groups[groupId]?.states?.asStateFlow() ?: error("Unknown group: $groupId")

    override suspend fun probeAll(groupId: String) {
        val g = groups[groupId] ?: return
        val updated = g.states.value.map { current ->
            val newHealth = runCatching { healthProbe.probe(current.mirror) }
                .getOrDefault(HealthState.UNHEALTHY)
            MirrorState(
                current.mirror,
                newHealth,
                lastCheck = clock.now(),
                consecutiveFailures = if (newHealth == HealthState.HEALTHY) 0 else current.consecutiveFailures,
            )
        }
        g.states.value = updated
    }

    override suspend fun reportSuccess(endpoint: MirrorUrl) = mutex.withLock {
        val g = groupContaining(endpoint) ?: return
        g.states.update { list ->
            list.map { s ->
                if (s.mirror == endpoint) {
                    s.copy(health = HealthState.HEALTHY, consecutiveFailures = 0, lastCheck = clock.now())
                } else {
                    s
                }
            }
        }
    }

    override suspend fun reportFailure(endpoint: MirrorUrl, cause: Throwable) = mutex.withLock {
        val g = groupContaining(endpoint) ?: return
        g.states.update { list ->
            list.map { s ->
                if (s.mirror != endpoint) {
                    s
                } else {
                    val newCount = s.consecutiveFailures + 1
                    val newHealth = when {
                        newCount >= policy.unhealthyAfter -> HealthState.UNHEALTHY
                        newCount >= policy.degradeAfter -> HealthState.DEGRADED
                        else -> s.health
                    }
                    s.copy(health = newHealth, consecutiveFailures = newCount, lastCheck = clock.now())
                }
            }
        }
    }

    private fun groupContaining(endpoint: MirrorUrl): Group? =
        groups.values.firstOrNull { g -> g.states.value.any { it.mirror == endpoint } }

    private fun healthRank(h: HealthState): Int = when (h) {
        HealthState.HEALTHY -> 0
        HealthState.DEGRADED -> 1
        HealthState.UNKNOWN -> 2
        HealthState.UNHEALTHY -> 3
    }
}
