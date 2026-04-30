package lava.sdk.api

import com.google.common.truth.Truth.assertThat
import kotlin.time.Duration.Companion.seconds
import org.junit.Test

class FallbackPolicyTest {
    @Test
    fun `defaults match documented values`() {
        val p = FallbackPolicy()
        assertThat(p.maxAttempts).isEqualTo(3)
        assertThat(p.perAttemptTimeout).isEqualTo(10.seconds)
        assertThat(p.degradeAfter).isEqualTo(1)
        assertThat(p.unhealthyAfter).isEqualTo(3)
    }

    @Test
    fun `custom values are honored`() {
        val p = FallbackPolicy(
            maxAttempts = 5,
            perAttemptTimeout = 30.seconds,
            degradeAfter = 2,
            unhealthyAfter = 4,
        )
        assertThat(p.maxAttempts).isEqualTo(5)
        assertThat(p.perAttemptTimeout).isEqualTo(30.seconds)
    }
}
