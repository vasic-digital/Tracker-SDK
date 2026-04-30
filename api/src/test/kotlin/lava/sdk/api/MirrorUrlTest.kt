package lava.sdk.api

import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.json.Json
import org.junit.Test

class MirrorUrlTest {
    private val json = Json { prettyPrint = false }

    @Test
    fun `constructs with sensible defaults`() {
        val m = MirrorUrl(url = "https://example.com")
        assertThat(m.url).isEqualTo("https://example.com")
        assertThat(m.isPrimary).isFalse()
        assertThat(m.priority).isEqualTo(0)
        assertThat(m.protocol).isEqualTo(Protocol.HTTPS)
        assertThat(m.region).isNull()
    }

    @Test
    fun `serializes to JSON round-trip identical`() {
        val original = MirrorUrl(
            url = "https://primary.example",
            isPrimary = true,
            priority = 0,
            protocol = Protocol.HTTPS,
            region = "us-east",
        )
        val encoded = json.encodeToString(MirrorUrl.serializer(), original)
        val decoded = json.decodeFromString(MirrorUrl.serializer(), encoded)
        assertThat(decoded).isEqualTo(original)
    }

    @Test
    fun `equality is structural`() {
        val a = MirrorUrl(url = "https://x")
        val b = MirrorUrl(url = "https://x")
        assertThat(a).isEqualTo(b)
        assertThat(a.hashCode()).isEqualTo(b.hashCode())
    }
}
