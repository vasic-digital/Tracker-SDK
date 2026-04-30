package lava.sdk.testing

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class HtmlFixtureLoaderTest {

    @Test
    fun `loads fixture from classpath resource`() {
        val loader = HtmlFixtureLoader(resourceRoot = "fixtures")
        val html = loader.load("sample.html")
        assertThat(html).contains("<h1>Sample</h1>")
    }

    @Test
    fun `throws on missing fixture with helpful message`() {
        val loader = HtmlFixtureLoader(resourceRoot = "fixtures")
        try {
            loader.load("missing.html")
            error("expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertThat(e.message).contains("missing.html")
            assertThat(e.message).contains("fixtures")
        }
    }
}
