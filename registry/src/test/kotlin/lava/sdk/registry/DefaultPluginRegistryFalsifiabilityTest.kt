package lava.sdk.registry

import com.google.common.truth.Truth.assertThat
import lava.sdk.api.HasId
import lava.sdk.api.MapPluginConfig
import lava.sdk.api.PluginConfig
import org.junit.Test

class DefaultPluginRegistryFalsifiabilityTest {

    private data class D(override val id: String) : HasId

    private fun factoryReturning(value: String) = object : PluginFactory<D, String> {
        override val descriptor = D("x")
        override fun create(config: PluginConfig) = value
    }

    @Test
    fun `re-registering same id replaces the factory (last-write-wins)`() {
        val reg = DefaultPluginRegistry<D, String>()
        reg.register(factoryReturning("first"))
        reg.register(factoryReturning("second"))
        val result = reg.get("x", MapPluginConfig())
        assertThat(result).isEqualTo("second")
    }
}
