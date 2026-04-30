package lava.sdk.testing

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Evidence record emitted by [falsifiabilityRehearsal] for the Sixth Law clause 6.A
 * "real-binary contract / real-feature falsifiability rehearsal" requirement.
 *
 * Consumers persist these records (typically as JSON under `.lava-ci-evidence/`)
 * so the audit trail of "we proved this test fails when its target is broken"
 * survives beyond the local test run.
 *
 * Fields:
 *  - [testName]: human-readable identity of the test under rehearsal.
 *  - [mutationDescription]: what was deliberately broken (e.g. "removed UNHEALTHY filter").
 *  - [observedFailureMessage]: the assertion / exception message produced by the
 *    test when run with the mutation applied. The literal substring
 *    `FALSIFIABILITY VIOLATION` appears here when the mutation did NOT cause
 *    a failure — i.e. the test is a bluff and would not catch the real defect.
 *  - [revertedSuccessfully]: true if the test passes once the mutation is reverted.
 *  - [recordedAt]: wall-clock instant the rehearsal completed.
 *  - [ledgerClause]: anchor in the constitutional ledger (default `6.A`).
 */
@Serializable
data class RehearsalRecord(
    val testName: String,
    val mutationDescription: String,
    val observedFailureMessage: String,
    val revertedSuccessfully: Boolean,
    val recordedAt: Instant = Clock.System.now(),
    val ledgerClause: String = "6.A",
)
