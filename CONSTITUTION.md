# Tracker-SDK Constitution

This document is the formal constitutional articulation of the rules
encoded in `CLAUDE.md`. All articles below are binding on every commit,
every pull request, and every release tag in this submodule.

## Article 1: Inherited Rules

This submodule inherits all constitutional rules from the parent Lava
project at https://github.com/milos85vasic/Lava/blob/master/CLAUDE.md.
Read that file first; this constitution adds rules specific to this
submodule and does not relax any inherited rule.

The following inherited rules are binding:

- **Sixth Law (clauses 6.A–6.F)** — all anti-bluff testing requirements.
  Tests in this submodule must traverse the same surfaces a consumer
  touches; falsifiability rehearsals are recorded; capability honesty
  applies (a primitive that exposes a feature must work).
- **Seventh Law (all 7 clauses)** — anti-bluff enforcement. Bluff-Audit
  stamps on every test commit; real-stack verification gate; pre-tag
  attestation; forbidden test patterns; recurring bluff hunts; bluff
  discovery protocol; recursive inheritance to all submodules.
- **Local-Only CI/CD** — no `.github/workflows/*`, no `.gitlab-ci.yml`,
  no GitVerse pipelines, no GitFlic CI. The `scripts/ci.sh` in this
  repo is the only quality gate; it runs locally only.
- **Decoupled Reusable Architecture** — applies recursively. If this
  submodule grows code that another vasic-digital project would
  reasonably reuse, that code is extracted to a deeper submodule.

## Article 2: No Domain Shape

This submodule MUST NOT contain any class, function, file, source-set
package name, or test resource that names a specific tracker, torrent
site, scraper target, or other Lava-domain entity. The submodule is
the **generic** layer; consuming projects (e.g. Lava) bring their own
domain shape on top.

The CI gate in `scripts/ci.sh` enforces this rule mechanically by
greping for forbidden domain-shape tokens.

The `tracker` word itself is permitted in **generic** contexts (e.g.,
"this primitive is useful for any tracker-style client"). Specific
tracker names are not. The CI gate is calibrated accordingly.

## Article 3: Versioning

This submodule uses semantic versioning. The pin in any consuming repo
is **frozen by default**; updating the pin is a deliberate PR. Breaking
changes require a major version bump and explicit consumer migration
notes in `CHANGELOG.md`.

## Article 4: Mirroring

This submodule is mirrored to GitHub + GitLab (current scope; 2-upstream
spec deviation 2026-04-30 reduced from the original 4-upstream policy).
The `scripts/sync-mirrors.sh` in this repo handles both upstreams; see
Lava's `scripts/sync-tracker-sdk-mirrors.sh` for the consumer-side
counterpart.

All upstreams MUST converge on the same SHA before any release tag is
cut. The sync script enforces this convergence check post-push.

## Article 5: Local-Only CI/CD

No hosted CI configuration is permitted in this repository. The
`scripts/ci.sh` script is the sole quality gate, runnable on any
developer machine. The pre-push hook (`.githooks/pre-push`) invokes
this script and is enabled via `git config core.hooksPath .githooks`.

Bypassing the pre-push hook with `--no-verify` is forbidden in routine
work and reserved for documented emergencies only.

## Clause 6.L — Anti-Bluff Functional Reality Mandate (Operator's Standing Order)

Inherited verbatim from parent Lava `/CLAUDE.md` §6.L. The operator has invoked this mandate **TEN TIMES** across two working days. The 10th invocation (2026-05-05, after Phase 7 readiness was reported, when the operator commissioned the full rebuild-and-test-everything cycle for tag Lava-Android-1.2.3): "Rebuild Go API and client app(s), put new builds into releases dir (with properly updated version codes) and execute all existing tests and Challenges! Any issue that pops up MUST BE properly addressed by addressing the root causes (fixing them) and covering everything with validation and verification tests and Challenges!"

Every test, every Challenge Test, every CI gate added to or maintained in this submodule MUST do exactly one job: confirm the feature it claims to cover actually works for an end user, end-to-end, on the gating matrix. CI green is necessary, NEVER sufficient. Tests must guarantee the product works — anything else is theatre.

Inheritance is recursive. Sub-submodules MAY paste this clause verbatim; they MUST NOT abbreviate or relax it.
