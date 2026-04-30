package lava.sdk.testing

import kotlinx.coroutines.flow.Flow
import lava.sdk.api.HealthState
import lava.sdk.api.MirrorState
import lava.sdk.api.MirrorUrl
import lava.sdk.mirror.DefaultMirrorManager
import lava.sdk.mirror.MirrorGroup
import lava.sdk.mirror.MirrorManager

/**
 * Test double for [MirrorManager] that delegates to a real
 * [DefaultMirrorManager] backed by a [FakeHealthProbe], and adds a
 * [setHealth] hook for tests to flip a mirror's reported health on the fly.
 *
 * Behaviourally equivalent to production for everything except probe sourcing:
 * fallback ordering, success/failure transitions, and observeHealth() all
 * exercise the real `DefaultMirrorManager` code paths (Anti-Bluff Third Law).
 *
 * TODO: SP-3a single-group simplification — [setHealth] re-probes a single
 * hard-coded group id. Multi-group fakes will subclass and expose the full
 * group set.
 */
class FakeMirrorManager(groups: List<MirrorGroup>) : MirrorManager {

    val probe = FakeHealthProbe()

    private val groupIds: List<String> = groups.map { it.groupId }
    private val delegate = DefaultMirrorManager(initialGroups = groups, healthProbe = probe)

    /**
     * Override the health [state] reported for the mirror identified by [url],
     * then re-probe every registered group so downstream observers see the
     * new state immediately.
     */
    suspend fun setHealth(url: String, state: HealthState) {
        probe.setState(url, state)
        groupIds.forEach { delegate.probeAll(it) }
    }

    override suspend fun getHealthyMirror(groupId: String): MirrorUrl? =
        delegate.getHealthyMirror(groupId)

    override suspend fun <T> executeWithFallback(
        groupId: String,
        op: suspend (MirrorUrl) -> T,
    ): T = delegate.executeWithFallback(groupId, op)

    override fun observeHealth(groupId: String): Flow<List<MirrorState>> =
        delegate.observeHealth(groupId)

    override suspend fun probeAll(groupId: String) = delegate.probeAll(groupId)

    override suspend fun reportSuccess(endpoint: MirrorUrl) =
        delegate.reportSuccess(endpoint)

    override suspend fun reportFailure(endpoint: MirrorUrl, cause: Throwable) =
        delegate.reportFailure(endpoint, cause)
}
