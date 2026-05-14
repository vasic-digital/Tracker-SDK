# AGENTS.md (Tracker-SDK submodule)

Agent guide for working in this submodule. Read this before making any
changes.

## Quick orientation

- This submodule is **generic SDK primitives** — no tracker-specific
  code. If you find yourself typing the name of a specific tracker
  site, torrent index, or scraper target, you're in the wrong repo.
  Submit the change to the consuming project (Lava) instead.
- All quality gates run locally via `./scripts/ci.sh`.
- All upstreams must converge on the same SHA before a release tag is
  cut. `./scripts/sync-mirrors.sh` enforces this.

## Common tasks

### Add a new primitive

1. Pick the right module: `:api` (types), `:mirror` (mirror logic),
   `:registry` (plugin discovery), or `:testing` (fakes/fixtures).
2. Create the new file under the module's `src/main/kotlin/lava/sdk/<module>/`.
3. Write a real-stack test under `src/test/kotlin/...`. Tests must
   exercise the public API; do not mock internal implementation.
4. If the primitive can be misused or is contract-shaped, add a
   `falsifiabilityRehearsal` test in `:testing`.
5. Run `./scripts/ci.sh` locally. All gates must pass.
6. Commit. Push to all upstreams via `./scripts/sync-mirrors.sh`.
7. If the change is API-breaking, bump major version and add a
   `CHANGELOG.md` entry.

### Run the full local CI gate

```bash
./scripts/ci.sh
```

This runs: spotless / ktlint, unit tests, no-domain-shape grep,
`forbidden hosted-CI files` check, license header check, mutation
tests on `:api` and `:mirror`.

### Add a new module

1. Create the directory under the repo root.
2. Add `include(":<module>")` to `settings.gradle.kts`.
3. Create `<module>/build.gradle.kts` matching the pattern of an
   existing module.
4. Document the module in `README.md` and `CLAUDE.md`.

## Things to avoid

- Importing OkHttp, Ktor, or any Android-specific library into `:api`.
  `:api` is pure Kotlin/JVM with no I/O dependencies.
- Adding a domain-specific (tracker-named) type or method anywhere.
- Hardcoded URLs of any kind. Even examples in tests use placeholder
  hosts (`example.com`, `mirror1.example`, etc.).
- Bypassing `./scripts/ci.sh` with `--no-verify`. The pre-push hook is
  not optional.

## Seventh Law — Anti-Bluff Enforcement

The full authoritative text lives in the parent project's `CLAUDE.md`
and `AGENTS.md` (Lava monorepo root). All seven clauses apply
recursively to this submodule:

1. **Bluff-Audit Stamp** on every test commit (`*_test.go`, `*_test.kt`).
2. **Real-Stack Verification Gate** for every user-visible primitive.
3. **Pre-Tag Attestation** via `./scripts/pretag-verify.sh`.
4. **Forbidden Test Patterns** — no SUT mocking, no verify-only primary assertions.
5. **Recurring Bluff Hunt** — phase-end ritual, output to `.lava-ci-evidence/bluff-hunt/`.
6. **Bluff Discovery Protocol** — incident recording in `.lava-ci-evidence/sixth-law-incidents/`.
7. **Inheritance** — applies to every submodule artifact; stricter rules allowed, relaxation forbidden.

## Anti-Bluff Functional Reality Mandate (Operator's Standing Order — Constitutional clause 6.L)

Inherited verbatim from parent Lava `/CLAUDE.md` §6.L. The operator has invoked this mandate **TWENTY-THREE TIMES** across two working days; the repetition itself is the forensic record. The 10th invocation (2026-05-05, immediately after Phase 7 readiness was reported, when the operator commissioned the full rebuild-and-test-everything cycle for tag Lava-Android-1.2.3): "Rebuild Go API and client app(s), put new builds into releases dir (with properly updated version codes) and execute all existing tests and Challenges! Any issue that pops up MUST BE properly addressed by addressing the root causes (fixing them) and covering everything with validation and verification tests and Challenges!"

Every test, every Challenge Test, every CI gate added to or maintained in this submodule has exactly one job: confirm the feature it claims to cover actually works for an end user, end-to-end, on the gating matrix. CI green is necessary, NEVER sufficient. Tests must guarantee the product works — anything else is theatre. If you find yourself rationalizing a "small exception" — STOP. There are no small exceptions. The Internet Archive stuck-on-loading bug, the broken post-login navigation, the credential leak in C2, the bluffed C1-C8 — these are what "small exceptions" produce.

Inheritance is recursive: this clause applies to every dependency, every test, every Challenge, every CI gate this submodule introduces. Sub-submodules MAY paste this clause verbatim; they MUST NOT abbreviate it.

## Clause 6.O (added 2026-05-05, inherited per 6.F)

- **Clause 6.O — Crashlytics-Resolved Issue Coverage Mandate** — see root `/CLAUDE.md` §6.O. Every Crashlytics-recorded issue (fatal OR non-fatal) closed/resolved by any commit MUST gain (a) a validation test in the language of the crashing surface that reproduces the conditions, (b) a Challenge Test under `app/src/androidTest/kotlin/lava/app/challenges/` (client) or `tests/e2e/` (server) that drives the same user-facing path, and (c) a closure log at `.lava-ci-evidence/crashlytics-resolved/<date>-<slug>.md` recording the issue ID, root-cause analysis, fix commit SHA, and links to the tests. `scripts/tag.sh` MUST refuse release tags whose CHANGELOG mentions Crashlytics fixes without matching closure logs. Marking a Crashlytics issue "closed" in the Console requires the test coverage to land first — never close-mark before the regression-immunity tests exist. Forensic anchor: 2026-05-05, 2 Crashlytics-recorded crashes within minutes of the first Firebase-instrumented APK distribution (Lava-Android-1.2.3-1023, commit `e9de508`); post-mortem at `.lava-ci-evidence/crashlytics-resolved/2026-05-05-firebase-init-hardening.md`. The operator's ELEVENTH §6.L invocation made this clause load-bearing.

## Clause 6.P (added 2026-05-05, inherited per 6.F)

- **Clause 6.P — Distribution Versioning + Changelog Mandate** — see root `/CLAUDE.md` §6.P. Every distribute action (Firebase App Distribution, container registry pushes, releases/ snapshots, scripts/tag.sh) MUST: (1) carry a strictly increasing versionCode (no re-distribution of already-published codes); (2) include a CHANGELOG entry — canonical file `CHANGELOG.md` at repo root + per-version snapshot at `.lava-ci-evidence/distribute-changelog/<channel>/<version>-<code>.md`; (3) inject the changelog into the App Distribution release-notes via `--release-notes`. `scripts/firebase-distribute.sh` REFUSES to operate when current versionCode ≤ last-distributed versionCode for the channel, OR when CHANGELOG.md lacks an entry for the current version, OR when the per-version snapshot file is missing. `scripts/tag.sh` enforces the same gates pre-tag. Re-distributing the same versionCode is forbidden across distribute sessions; idempotent retry within a single session is permitted. Forensic anchor: 2026-05-05 23:11 operator's TWELFTH §6.L invocation: "when distributing new build it must have version code bigger by at least one then the last version code available for download (already distribited). Every distributed build MUST CONTAIN changelog with the details what it includes compared to previous one we have published!"

## Clause 6.Q (added 2026-05-05, inherited per 6.F)

- **Clause 6.Q — Compose Layout Antipattern Guard** — see root `/CLAUDE.md` §6.Q. Forbids nesting vertically-scrolling lazy layouts (LazyColumn, LazyVerticalGrid, LazyVerticalStaggeredGrid) inside parents giving unbounded vertical space (verticalScroll, unbounded wrapContentHeight, LinearLayout-with-weight wrapper). Equivalent rule horizontally for LazyRow / LazyHorizontalGrid / LazyHorizontalStaggeredGrid. Per-feature structural tests + Compose UI Challenge Tests on the §6.I matrix are the load-bearing acceptance gates. Forensic anchor: 2026-05-05 23:51 operator-reported "Opening Trackers from Settings crashes the app" — TrackerSelectorList used LazyColumn nested in TrackerSettingsScreen's Column(verticalScroll). Closure log: `.lava-ci-evidence/crashlytics-resolved/2026-05-05-tracker-settings-nested-scroll.md`. Pattern guard: `feature/tracker_settings/src/test/.../TrackerSelectorListLazyColumnRegressionTest.kt`. The operator THIRTEENTH §6.L invocation triggered this clause.

## §6.T — Universal Quality Constraints (inherited 2026-05-06, per §6.F)

See root `/CLAUDE.md` §6.T. All four sub-points (Reproduction-Before-Fix, Resource Limits for Tests & Challenges, No-Force-Push, Bugfix Documentation) apply verbatim. This submodule MAY add stricter rules but MUST NOT relax any of §6.T.1–§6.T.4.

## §6.X — Container-Submodule Emulator Wiring Mandate (inherited 2026-05-13, per §6.F)

Inherited verbatim from parent Lava `/CLAUDE.md` §6.X (added 2026-05-13 in response to the operator's twenty-first §6.L invocation: "when we rely / depend on emulator(s) needed for the testing of the System, make sure we boot up Container running Android emulator in it using ours Containers Submodule. It is supported and it works, it just need proper connecting into the flows."). Every Android emulator instance MUST execute INSIDE a podman/docker container managed by `Submodules/Containers/`. Host-direct emulator launches are permitted for workstation iteration only; the constitutional gate run (release tagging, real-device verification) MUST go through the container-bound path. `pkg/runtime/` brings the container up; `pkg/emulator/` orchestrates the AVD lifecycle inside it. §6.X-debt tracks the wiring implementation owed to the Containers submodule. This submodule MAY add stricter rules but MUST NOT relax.

## §6.Z — Anti-Bluff Distribute Guard (inherited 2026-05-14, per §6.F)

See root `/CLAUDE.md` §6.Z. No artifact may be distributed (Firebase App Distribution, Google Play Store release, container image push, this submodule's binary release, any future channel) UNLESS the corresponding end-to-end tests have been **EXECUTED — not source-compiled, EXECUTED** — against the EXACT artifact about to be distributed, AND have **passed**. Pre-distribute test-evidence file required at `.lava-ci-evidence/distribute-changelog/<channel>/<version>-<code>-test-evidence.{md,json}` with matching commit SHA, timestamp within 24h, `BUILD SUCCESSFUL` (or per-language pass marker) verbatim in captured output. Cold-start verification is the load-bearing canary. Distributing a faulty version is a constitutional violation by construction. §6.Z-debt is open: mechanical enforcement via `scripts/firebase-distribute.sh` Phase 1 Gate 6 + pre-push hook check is documented but not yet enforced. Forensic anchor: 2026-05-14 Galaxy S23 Ultra cold-launch crash on Lava-Android-1.2.19-1039 (Crashlytics `40a62f97a5c65abb56142b4ca2c37eeb` — `painterResource()` rejection of `<layer-list>` drawable); agent had skipped Compose UI test execution citing the wrong §6.X caveat. Operator's 26th §6.L invocation: "Anti-bluff policy MUST BE ENFORCED ALWAYS!!!" This submodule MAY add stricter rules but MUST NOT relax this clause.
## §6.AA — Two-Stage Distribute Mandate (inherited 2026-05-14, per §6.F)

See root `/CLAUDE.md` §6.AA. When an artifact has both a debug and a release variant (or analogous dev-vs-prod build types — including this submodule's binary release if it ships separate dev / prod variants), distribute MUST happen in TWO STAGES with operator-confirmed verification between them. Stage 1 distributes the debug / dev variant only; the operator verifies the **distributed** debug variant on the failure-surface device class. Stage 2 distributes the release / prod variant only ONLY AFTER written stage-1 verification, with the §6.Z test-evidence file appended with a `release-stage` section. No combined distribute permitted by default; the combined path requires explicit per-cycle operator authorization recorded in the evidence file. The R8 / minification surprise class on Android (or analogous stripping / production-only optimization classes on other artifacts) is the load-bearing reason. §6.AA-debt is open: mechanical enforcement via `scripts/firebase-distribute.sh` default flip + refusal of out-of-order `--release-only` + paired `last-version-{debug,release}` per-channel pre-push check is documented but not yet enforced. Forensic anchor: 2026-05-14 operator directive immediately after the §6.Z forensic-anchor crash on Lava-Android-1.2.19-1039: "for purposes like this one we shall distribute via Firebase DEV / DEBUG version only. Once we try it, you continue and once all verified you distribute RELEASE too!" This submodule MAY add stricter rules but MUST NOT relax this clause.
## §6.AB — Anti-Bluff Test-Suite Reinforcement (inherited 2026-05-14, per §6.F)

See root `/CLAUDE.md` §6.AB. Every existing test + Challenge in this submodule MUST be auditable for the anti-bluff property "would this test fail if the user-visible behavior broke in a way a real user would notice?" Per-feature completeness checklist: rendering correctness (assert dominant color matches expected hue, not just RGB-variance), state-machine completeness (negative tests for forbidden transitions), gating logic (gate fires only on actual completion criterion). Bluff-hunt cadence escalation: every defect not caught by an existing test triggers a 5-file defect-driven hunt of adjacent tests, recorded under `.lava-ci-evidence/bluff-hunt/<date>-defect-driven-<slug>.json`. Discrimination test mandatory per Challenge Test: deliberately-broken-but-non-crashing production code MUST cause the Challenge Test to fail. Forensic anchor: 2026-05-14 Lava-Android-1.2.20-1040 white-icon + onboarding-gate-bypass — both passed all existing tests but failed for the user. Operator's 27th §6.L invocation: "all existing tests and Challenges do work in anti-bluff manner — they MUST confirm that all tested codebase really works as expected!" This submodule MAY add stricter rules but MUST NOT relax this clause.
## §6.AC — Comprehensive Non-Fatal Telemetry Mandate (inherited 2026-05-14, per §6.F)

See root `/CLAUDE.md` §6.AC. Every catch / error / fallback / unexpected-state path on every distributable artifact in this submodule MUST record a non-fatal telemetry event with sufficient context to triage the failure remotely. The Android-side canonical entry is `analytics.recordNonFatal(throwable, ctx)` / `analytics.recordWarning(message, ctx)` (lava.common.analytics.AnalyticsTracker); the Go-side equivalent is `observability.RecordNonFatal(ctx, err, attrs)`. Cancellation throwables are filtered automatically. Mandatory context: feature/module + operation + error_class + error_message (truncated 1024, no credentials per §6.H) + per-platform extras. Forbidden: silent fallbacks without telemetry; credentials/tokens/cookies/PII unredacted in event attributes. §6.AC-debt is open: Detekt + Go-vet rules flagging `try/catch` blocks lacking the telemetry call, pre-push hook integration. Forensic anchor: 2026-05-14 operator: "Add comprehensive Crashlytics non-fatals recording all over the apps and API so we can track in the background all warnings, issues and unexpected situations!" This submodule MAY add stricter rules but MUST NOT relax this clause.
