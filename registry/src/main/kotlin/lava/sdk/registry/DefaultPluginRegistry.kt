package lava.sdk.registry

import java.util.concurrent.ConcurrentHashMap
import lava.sdk.api.HasId
import lava.sdk.api.PluginConfig

/**
 * Default thread-safe [PluginRegistry] backed by a [ConcurrentHashMap].
 *
 * Re-registering the same id replaces the previous factory (last-write-wins).
 * [get] throws [IllegalArgumentException] for unknown ids.
 */
class DefaultPluginRegistry<D : HasId, P> : PluginRegistry<D, P> {

    private val factories = ConcurrentHashMap<String, PluginFactory<D, P>>()

    override fun register(factory: PluginFactory<D, P>) {
        factories[factory.descriptor.id] = factory
    }

    override fun unregister(id: String) {
        factories.remove(id)
    }

    override fun get(id: String, config: PluginConfig): P {
        val f = factories[id]
            ?: throw IllegalArgumentException("Unknown plugin id: $id")
        return f.create(config)
    }

    override fun list(): List<D> = factories.values.map { it.descriptor }

    override fun isRegistered(id: String): Boolean = factories.containsKey(id)
}
