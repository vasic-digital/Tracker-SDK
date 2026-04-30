package lava.sdk.mirror

import kotlinx.coroutines.test.runTest
import lava.sdk.api.HealthState
import lava.sdk.api.MirrorUnavailableException
import lava.sdk.api.MirrorUrl
import org.junit.Test

/**
 * Falsifiability rehearsal for the UNHEALTHY skip in
 * [DefaultMirrorManager.executeWithFallback].
 *
 * The production code blocks UNHEALTHY mirrors via two cooperating
 * mechanisms: (a) a `(healthRank, priority)` sort that orders HEALTHY first,
 * and (b) an explicit `.filter { it.health != HealthState.UNHEALTHY }` that
 * removes UNHEALTHY mirrors from the candidate list entirely.
 *
 * Mechanism (a) alone is not enough. When *every* mirror in a group is
 * UNHEALTHY, the sort cannot demote any of them — only the filter can keep
 * them out of the fallback chain. This rehearsal exercises that exact case
 * so removing only the filter line produces an observable test failure.
 */
class DefaultMirrorManagerFalsifiabilityTest {

    @Test
    fun `fallback never invokes op when every mirror is UNHEALTHY (filter is load-bearing)`() = runTest {
        val mgr = DefaultMirrorManager(
            initialGroups = listOf(
                MirrorGroup(
                    groupId = "g",
                    mirrors = listOf(
                        MirrorUrl("https://a", isPrimary = true, priority = 0),
                        MirrorUrl("https://b", priority = 1),
                    ),
                    expectedMarker = "marker",
                ),
            ),
            healthProbe = object : HealthProbe {
                override suspend fun probe(endpoint: MirrorUrl) = HealthState.UNHEALTHY
            },
        )
        mgr.probeAll("g")

        val touched = mutableListOf<String>()
        val thrown = runCatching {
            mgr.executeWithFallback("g") { m ->
                touched += m.url
                "ok-${m.url}"
            }
        }.exceptionOrNull()

        // Primary user-visible assertion: when all mirrors are UNHEALTHY,
        // op MUST NEVER be invoked, and the fallback MUST raise
        // MirrorUnavailableException with an empty `tried` list. If the
        // filter is removed, both mirrors get tried (touched != []) and the
        // exception's `tried` list reports the attempts.
        check(touched.isEmpty()) {
            "expected op to be invoked zero times (all mirrors UNHEALTHY); got touched=$touched"
        }
        check(thrown is MirrorUnavailableException) {
            "expected MirrorUnavailableException; got $thrown"
        }
        check((thrown as MirrorUnavailableException).tried.isEmpty()) {
            "expected MirrorUnavailableException.tried to be empty (filter skipped all); " +
                "got tried=${thrown.tried.map { it.url }}"
        }
    }
}
