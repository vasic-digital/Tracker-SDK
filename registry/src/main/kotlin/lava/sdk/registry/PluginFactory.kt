package lava.sdk.registry

import lava.sdk.api.HasId
import lava.sdk.api.PluginConfig

/**
 * Produces an instance of a plugin [P] keyed by an [HasId] descriptor [D].
 * The descriptor's [HasId.id] is the registration key used by [PluginRegistry].
 */
interface PluginFactory<D : HasId, P> {
    val descriptor: D
    fun create(config: PluginConfig): P
}
