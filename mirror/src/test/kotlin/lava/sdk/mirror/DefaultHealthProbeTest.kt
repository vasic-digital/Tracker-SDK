package lava.sdk.mirror

import com.google.common.truth.Truth.assertThat
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.test.runTest
import lava.sdk.api.HealthState
import lava.sdk.api.MirrorUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test

class DefaultHealthProbeTest {
    private lateinit var server: MockWebServer

    @Before
    fun setUp() {
        server = MockWebServer().also { it.start() }
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `200 with body containing marker - HEALTHY`() = runTest {
        server.enqueue(MockResponse().setBody("Welcome to ExpectedMarker site"))
        val probe = DefaultHealthProbe(
            expectedMarker = "ExpectedMarker",
            timeout = 5.seconds,
        )
        val result = probe.probe(MirrorUrl(server.url("/").toString()))
        assertThat(result).isEqualTo(HealthState.HEALTHY)
    }

    @Test
    fun `200 without marker - UNHEALTHY (captive-portal hazard)`() = runTest {
        server.enqueue(MockResponse().setBody("This site is blocked"))
        val probe = DefaultHealthProbe("ExpectedMarker", 5.seconds)
        val result = probe.probe(MirrorUrl(server.url("/").toString()))
        assertThat(result).isEqualTo(HealthState.UNHEALTHY)
    }

    @Test
    fun `500 - UNHEALTHY`() = runTest {
        server.enqueue(MockResponse().setResponseCode(500).setBody("Internal Error"))
        val probe = DefaultHealthProbe("ExpectedMarker", 5.seconds)
        val result = probe.probe(MirrorUrl(server.url("/").toString()))
        assertThat(result).isEqualTo(HealthState.UNHEALTHY)
    }

    @Test
    fun `slow response (5-10s) - DEGRADED`() = runTest {
        server.enqueue(
            MockResponse()
                .setBody("ExpectedMarker present")
                .setHeadersDelay(7, TimeUnit.SECONDS),
        )
        val probe = DefaultHealthProbe("ExpectedMarker", 15.seconds)
        val result = probe.probe(MirrorUrl(server.url("/").toString()))
        assertThat(result).isEqualTo(HealthState.DEGRADED)
    }
}
