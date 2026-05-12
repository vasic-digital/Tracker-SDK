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

Inherited verbatim from parent Lava `/CLAUDE.md` §6.L. The operator has invoked this mandate **SEVENTEEN TIMES** across two working days; the repetition itself is the forensic record. The 10th invocation (2026-05-05, immediately after Phase 7 readiness was reported, when the operator commissioned the full rebuild-and-test-everything cycle for tag Lava-Android-1.2.3): "Rebuild Go API and client app(s), put new builds into releases dir (with properly updated version codes) and execute all existing tests and Challenges! Any issue that pops up MUST BE properly addressed by addressing the root causes (fixing them) and covering everything with validation and verification tests and Challenges!"

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
