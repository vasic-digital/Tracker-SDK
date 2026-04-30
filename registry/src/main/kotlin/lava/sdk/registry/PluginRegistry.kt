package lava.sdk.registry

import lava.sdk.api.HasId
import lava.sdk.api.PluginConfig

/**
 * Generic plugin registry keyed by descriptor id.
 *
 * Implementations must be safe for concurrent registration / lookup.
 * Re-registering the same id replaces the previous factory (last-write-wins).
 */
interface PluginRegistry<D : HasId, P> {
    fun register(factory: PluginFactory<D, P>)
    fun unregister(id: String)
    fun get(id: String, config: PluginConfig): P
    fun list(): List<D>
    fun isRegistered(id: String): Boolean
}
