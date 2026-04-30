package lava.sdk.api

/**
 * Opaque config bag passed to a [PluginFactory.create] call.
 * Implementations may extend with their own typed accessors.
 */
interface PluginConfig {
    val raw: Map<String, Any?>
}

/** Trivial implementation backed by a map. */
class MapPluginConfig(override val raw: Map<String, Any?> = emptyMap()) : PluginConfig
