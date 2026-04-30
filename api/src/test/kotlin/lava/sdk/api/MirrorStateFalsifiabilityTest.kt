package lava.sdk.api

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.Instant
import org.junit.Test

class MirrorStateFalsifiabilityTest {
    @Test
    fun `equality is structural — proves data class shape`() {
        val mirror = MirrorUrl("https://x")
        val now = Instant.fromEpochSeconds(1000)
        val a = MirrorState(mirror, HealthState.HEALTHY, now, 0)
        val b = MirrorState(mirror, HealthState.HEALTHY, now, 0)
        assertThat(a).isEqualTo(b)
        assertThat(a.hashCode()).isEqualTo(b.hashCode())
    }
}
