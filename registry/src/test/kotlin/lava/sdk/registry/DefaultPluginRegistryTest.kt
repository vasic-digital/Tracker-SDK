package lava.sdk.registry

import com.google.common.truth.Truth.assertThat
import lava.sdk.api.HasId
import lava.sdk.api.MapPluginConfig
import org.junit.Test

class DefaultPluginRegistryTest {
    private data class TestDescriptor(override val id: String, val displayName: String) : HasId
    private interface TestPlugin { val descriptor: TestDescriptor; fun greet(): String }

    private fun factory(d: TestDescriptor): PluginFactory<TestDescriptor, TestPlugin> =
        object : PluginFactory<TestDescriptor, TestPlugin> {
            override val descriptor = d
            override fun create(config: lava.sdk.api.PluginConfig) =
                object : TestPlugin {
                    override val descriptor = d
                    override fun greet() = "hi from ${d.id}"
                }
        }

    @Test
    fun `register then get returns same plugin instance per call`() {
        val reg = DefaultPluginRegistry<TestDescriptor, TestPlugin>()
        val d = TestDescriptor("alpha", "Alpha")
        reg.register(factory(d))
        val plugin = reg.get("alpha", MapPluginConfig())
        assertThat(plugin.greet()).isEqualTo("hi from alpha")
    }

    @Test
    fun `list returns all registered descriptors`() {
        val reg = DefaultPluginRegistry<TestDescriptor, TestPlugin>()
        reg.register(factory(TestDescriptor("a", "A")))
        reg.register(factory(TestDescriptor("b", "B")))
        assertThat(reg.list().map { it.id }).containsExactly("a", "b")
    }

    @Test
    fun `get on unknown id throws IllegalArgumentException`() {
        val reg = DefaultPluginRegistry<TestDescriptor, TestPlugin>()
        try {
            reg.get("missing", MapPluginConfig())
            error("expected exception")
        } catch (e: IllegalArgumentException) {
            assertThat(e.message).contains("missing")
        }
    }

    @Test
    fun `unregister removes the factory`() {
        val reg = DefaultPluginRegistry<TestDescriptor, TestPlugin>()
        val d = TestDescriptor("x", "X")
        reg.register(factory(d))
        reg.unregister("x")
        assertThat(reg.list()).isEmpty()
    }
}
