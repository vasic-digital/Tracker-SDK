package lava.sdk.testing

/**
 * Loads HTML fixtures from the test classpath under the configured [resourceRoot].
 *
 * Example: with `resourceRoot = "fixtures"`, `load("forum/topic.html")` reads
 * `/fixtures/forum/topic.html` from the classpath of the calling test module.
 *
 * Throws [IllegalArgumentException] with a message naming the relative path,
 * the configured root, and the resolved classpath lookup so missing fixtures
 * are diagnosable from the failure alone.
 */
class HtmlFixtureLoader(private val resourceRoot: String) {

    fun load(relativePath: String): String {
        val full = "/$resourceRoot/$relativePath"
        val stream = HtmlFixtureLoader::class.java.getResourceAsStream(full)
            ?: throw IllegalArgumentException(
                "Fixture not found: $relativePath under $resourceRoot (looked for $full)",
            )
        return stream.bufferedReader().use { it.readText() }
    }
}
