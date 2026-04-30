package lava.sdk.api

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.Instant
import org.junit.Test

class MirrorStateTest {
    @Test
    fun `constructs with required fields`() {
        val mirror = MirrorUrl("https://x")
        val now = Instant.fromEpochSeconds(1000)
        val s = MirrorState(
            mirror = mirror,
            health = HealthState.HEALTHY,
            lastCheck = now,
            consecutiveFailures = 0,
        )
        assertThat(s.mirror).isEqualTo(mirror)
        assertThat(s.health).isEqualTo(HealthState.HEALTHY)
        assertThat(s.consecutiveFailures).isEqualTo(0)
    }

    @Test
    fun `lastCheck may be null for never-probed mirror`() {
        val s = MirrorState(MirrorUrl("https://x"), HealthState.UNKNOWN, null, 0)
        assertThat(s.lastCheck).isNull()
    }
}
