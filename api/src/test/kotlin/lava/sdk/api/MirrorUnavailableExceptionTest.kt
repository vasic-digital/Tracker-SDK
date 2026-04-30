package lava.sdk.api

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MirrorUnavailableExceptionTest {
    @Test
    fun `carries list of attempted mirrors`() {
        val tried = listOf(MirrorUrl("https://a"), MirrorUrl("https://b"))
        val ex = MirrorUnavailableException(tried)
        assertThat(ex.tried).containsExactlyElementsIn(tried).inOrder()
        assertThat(ex.message).contains("2 mirror(s) attempted")
    }

    @Test
    fun `is a Throwable subtype usable in coroutine cancellation`() {
        val ex: Throwable = MirrorUnavailableException(emptyList())
        assertThat(ex).isInstanceOf(Exception::class.java)
    }
}
