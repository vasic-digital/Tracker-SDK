package lava.sdk.testing

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

/**
 * Loads JSON fixtures from the test classpath under the configured [resourceRoot].
 *
 * Use [loadString] for raw text and [loadElement] when the test wants to
 * navigate the parsed [JsonElement] tree.
 */
class JsonFixtureLoader(private val resourceRoot: String) {

    private val json = Json { ignoreUnknownKeys = true }

    fun loadElement(relativePath: String): JsonElement =
        json.parseToJsonElement(loadString(relativePath))

    fun loadString(relativePath: String): String {
        val full = "/$resourceRoot/$relativePath"
        return JsonFixtureLoader::class.java.getResourceAsStream(full)
            ?.bufferedReader()?.use { it.readText() }
            ?: throw IllegalArgumentException(
                "Fixture not found: $relativePath under $resourceRoot (looked for $full)",
            )
    }
}
