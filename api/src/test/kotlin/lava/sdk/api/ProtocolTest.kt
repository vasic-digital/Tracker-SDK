package lava.sdk.api

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ProtocolTest {
    @Test
    fun `enum has exactly three values`() {
        assertThat(Protocol.entries).hasSize(3)
        assertThat(Protocol.entries.map { it.name })
            .containsExactly("HTTP", "HTTPS", "HTTP3")
    }
}
