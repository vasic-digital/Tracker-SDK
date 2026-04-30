package lava.sdk.testing

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import lava.sdk.api.HealthState
import lava.sdk.api.MirrorUrl
import lava.sdk.mirror.MirrorGroup
import org.junit.Test

class FakeMirrorManagerTest {

    @Test
    fun `setHealth toggles mirror state visible to executeWithFallback`() = runTest {
        val mgr = FakeMirrorManager(
            listOf(
                MirrorGroup(
                    groupId = "g",
                    mirrors = listOf(
                        MirrorUrl("https://a", priority = 0),
                        MirrorUrl("https://b", priority = 1),
                    ),
                    expectedMarker = "marker",
                ),
            ),
        )
        mgr.setHealth("https://a", HealthState.UNHEALTHY)
        val tried = mutableListOf<String>()
        mgr.executeWithFallback("g") { m ->
            tried += m.url
            "ok"
        }
        assertThat(tried).containsExactly("https://b")
    }
}
