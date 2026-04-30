package lava.sdk.mirror

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import lava.sdk.api.HealthState
import lava.sdk.api.MirrorUnavailableException
import lava.sdk.api.MirrorUrl
import org.junit.Test

class DefaultMirrorManagerTest {

    private fun groupOf(vararg urls: String) = MirrorGroup(
        groupId = "g",
        mirrors = urls.mapIndexed { i, u -> MirrorUrl(u, isPrimary = i == 0, priority = i) },
        expectedMarker = "marker",
    )

    @Test
    fun `executeWithFallback returns first successful mirror result`() = runTest {
        val mgr = DefaultMirrorManager(
            initialGroups = listOf(groupOf("https://a", "https://b")),
            healthProbe = FakeProbe { _ -> HealthState.HEALTHY },
        )
        val tried = mutableListOf<String>()
        val result = mgr.executeWithFallback("g") { mirror ->
            tried += mirror.url
            "result-from-${mirror.url}"
        }
        assertThat(result).isEqualTo("result-from-https://a")
        assertThat(tried).containsExactly("https://a")
    }

    @Test
    fun `executeWithFallback skips UNHEALTHY mirror and tries next`() = runTest {
        val mgr = DefaultMirrorManager(
            initialGroups = listOf(groupOf("https://a", "https://b")),
            healthProbe = FakeProbe { mirror ->
                if (mirror.url == "https://a") HealthState.UNHEALTHY else HealthState.HEALTHY
            },
        )
        mgr.probeAll("g")
        val tried = mutableListOf<String>()
        val result = mgr.executeWithFallback("g") { mirror ->
            tried += mirror.url
            "result-from-${mirror.url}"
        }
        assertThat(tried).containsExactly("https://b")
        assertThat(result).isEqualTo("result-from-https://b")
    }

    @Test
    fun `executeWithFallback retries on per-attempt failure`() = runTest {
        val mgr = DefaultMirrorManager(
            initialGroups = listOf(groupOf("https://a", "https://b")),
            healthProbe = FakeProbe { _ -> HealthState.HEALTHY },
        )
        var calls = 0
        val result = mgr.executeWithFallback("g") { mirror ->
            calls++
            if (mirror.url == "https://a") error("a-failed") else "ok"
        }
        assertThat(calls).isEqualTo(2)
        assertThat(result).isEqualTo("ok")
    }

    @Test
    fun `executeWithFallback throws MirrorUnavailable when all fail`() = runTest {
        val mgr = DefaultMirrorManager(
            initialGroups = listOf(groupOf("https://a", "https://b")),
            healthProbe = FakeProbe { _ -> HealthState.HEALTHY },
        )
        try {
            mgr.executeWithFallback("g") { _ -> error("always-fail") }
            error("expected MirrorUnavailableException")
        } catch (e: MirrorUnavailableException) {
            assertThat(e.tried).hasSize(2)
        }
    }

    private class FakeProbe(private val states: (MirrorUrl) -> HealthState) : HealthProbe {
        override suspend fun probe(endpoint: MirrorUrl) = states(endpoint)
    }
}
