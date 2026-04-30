package lava.sdk.testing

/**
 * Run [runTest] twice: first with the mutation produced by [applyMutation]
 * applied, then with the mutation reverted (via the returned [AutoCloseable]).
 *
 * Returned [RehearsalRecord]:
 *  - If the mutated run threw, [RehearsalRecord.observedFailureMessage] holds
 *    that throwable's message and [RehearsalRecord.revertedSuccessfully] tells
 *    you whether the post-revert run passed (the expected, healthy outcome).
 *  - If the mutated run did NOT throw, the returned record's
 *    `observedFailureMessage` contains the literal string
 *    `FALSIFIABILITY VIOLATION: test passed under mutation` and
 *    `revertedSuccessfully` is `false` — that is, the test is a bluff: it
 *    cannot detect the very breakage the rehearsal aims to surface.
 *
 * Usage:
 * ```
 * val rec = falsifiabilityRehearsal(
 *   name = "fallback skips UNHEALTHY",
 *   mutationDescription = "removed UNHEALTHY filter from executeWithFallback",
 *   applyMutation = { /* temp edit; return AutoCloseable that reverts */ },
 *   runTest = { runUnderTest() },
 * )
 * evidenceWriter.write(rec)
 * ```
 *
 * The runner is given the mutation lifecycle as code (not as a comment), so
 * the rehearsal is repeatable, deterministic, and auditable — the load-bearing
 * primitive behind the Sixth Law clause 6.A "real-binary contract" rule.
 */
inline fun falsifiabilityRehearsal(
    name: String,
    mutationDescription: String,
    applyMutation: () -> AutoCloseable,
    runTest: () -> Unit,
): RehearsalRecord {
    val handle = applyMutation()
    val observedFailure = try {
        runTest()
        // If we reach here, the test passed under the mutation — that's a bluff.
        return RehearsalRecord(
            testName = name,
            mutationDescription = mutationDescription,
            observedFailureMessage = "FALSIFIABILITY VIOLATION: test passed under mutation",
            revertedSuccessfully = false,
        )
    } catch (t: Throwable) {
        t.message ?: t::class.simpleName ?: "(no message)"
    } finally {
        handle.close()
    }

    // Now run with mutation reverted; this should pass.
    val passedAfterRevert = try {
        runTest()
        true
    } catch (_: Throwable) {
        false
    }

    return RehearsalRecord(
        testName = name,
        mutationDescription = mutationDescription,
        observedFailureMessage = observedFailure,
        revertedSuccessfully = passedAfterRevert,
    )
}
