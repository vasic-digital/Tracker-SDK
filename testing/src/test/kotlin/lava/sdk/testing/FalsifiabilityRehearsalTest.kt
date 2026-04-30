package lava.sdk.testing

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class FalsifiabilityRehearsalTest {

    @Test
    fun `rehearsal returns record when mutation causes failure and revert restores`() {
        var brokenFlag = false
        val rec = falsifiabilityRehearsal(
            name = "demo",
            mutationDescription = "set brokenFlag",
            applyMutation = {
                brokenFlag = true
                AutoCloseable { brokenFlag = false }
            },
            runTest = {
                if (brokenFlag) error("system is broken")
                // else: pass
            },
        )
        assertThat(rec.testName).isEqualTo("demo")
        assertThat(rec.mutationDescription).isEqualTo("set brokenFlag")
        assertThat(rec.observedFailureMessage).contains("system is broken")
        assertThat(rec.revertedSuccessfully).isTrue()
    }

    @Test
    fun `rehearsal flags violation when mutation does not cause failure`() {
        val rec = falsifiabilityRehearsal(
            name = "tautology",
            mutationDescription = "set a flag nobody reads",
            applyMutation = { AutoCloseable { } },
            runTest = { /* always passes */ },
        )
        assertThat(rec.observedFailureMessage).contains("FALSIFIABILITY VIOLATION")
        assertThat(rec.revertedSuccessfully).isFalse()
    }
}
