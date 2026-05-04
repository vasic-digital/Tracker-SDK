# CLAUDE.md (Tracker-SDK submodule)

This file inherits all constitutional rules from the parent Lava project
at https://github.com/milos85vasic/Lava/blob/master/CLAUDE.md. Read that
file first; this document only adds rules specific to this submodule.

## Inherited rules (binding)

- **Sixth Law (clauses 6.A–6.F)** — all anti-bluff testing requirements.
  Tests in this submodule must traverse the same surfaces a consumer
  touches; falsifiability rehearsals are recorded; capability honesty
  applies (a primitive that exposes a feature must work).
- **Seventh Law (Anti-Bluff Enforcement, all 7 clauses)** — added by
  the parent Lava CLAUDE.md (2026-04-30) in response to the operator
  mandate that passing tests MUST guarantee user-reachable functionality.
  Every test commit in this submodule MUST carry a Bluff-Audit stamp;
  every primitive that claims a behavioural contract MUST have a
  real-stack verification gate (e.g. `MockWebServer`-driven for HTTP
  primitives, in-memory Postgres-driven for persistence primitives,
  real-cluster-driven for clustering primitives where the cluster is
  spun up via testcontainers); the bluff-hunt protocol applies; the
  forbidden test patterns list is binding. See parent CLAUDE.md
  "Seventh Law" section for the verbatim text.
- **Local-Only CI/CD** — no `.github/workflows/*`, no `.gitlab-ci.yml`,
  no GitVerse pipelines, no GitFlic CI. The `scripts/ci.sh` in this
  repo is the only quality gate; it runs locally only.
- **Decoupled Reusable Architecture** — applies recursively. If this
  submodule grows code that another vasic-digital project would
  reasonably reuse, that code is extracted to a deeper submodule.

## Submodule-specific rule: NO DOMAIN SHAPE

This submodule MUST NOT contain any class, function, file, source-set
package name, or test resource that names a specific tracker, torrent
site, scraper target, or other Lava-domain entity. The submodule is
the **generic** layer; consuming projects (e.g. Lava) bring their own
domain shape on top.

CI gate (in `scripts/ci.sh`):

```bash
forbidden=$(grep -rEi --exclude-dir=.git --exclude-dir=build \
  '(<forbidden-pattern>)' . | \
  grep -v 'docs/.*example' | grep -v 'README.md.*Lava project' || true)
if [[ -n "$forbidden" ]]; then
  echo "Domain-shape violation:"
  echo "$forbidden"
  exit 1
fi
```

The `tracker` word itself is permitted in **generic** contexts (e.g.,
"this primitive is useful for any tracker-style client"). Specific
tracker names are not. The CI gate is calibrated accordingly.

## Versioning

This submodule uses semantic versioning. The pin in any consuming repo
is **frozen by default**; updating the pin is a deliberate PR. Breaking
changes require a major version bump and explicit consumer migration
notes in `CHANGELOG.md`.

## Mirroring

This submodule is mirrored to GitHub + GitLab (current scope; 2-upstream
spec deviation 2026-04-30 reduced from the original 4-upstream policy).
The `scripts/sync-mirrors.sh` in this repo handles both upstreams; see
Lava's `scripts/sync-tracker-sdk-mirrors.sh` for the consumer-side
counterpart.

## Clauses 6.I and 6.J (added 2026-05-04, inherited per 6.F)

- **Clause 6.I — Multi-Emulator Container Matrix as Real-Device Equivalent** — see root `/CLAUDE.md` §6.I. Real-device verification, where this submodule's work requires it (per 6.G clause 5 / Sixth Law clause 5 / Seventh Law clause 3), is satisfied ONLY by the project's container-bound multi-emulator matrix: minimum API 28, 30, 34, latest stable; minimum phone + tablet + (where applicable) TV; per-AVD attestation rows in `.lava-ci-evidence/<tag>/real-device-verification.md`; cold-boot for the gate; falsifiability rehearsal per Android-version-class. A single passing emulator (or device) is NOT the gate — the matrix is.
- **Clause 6.J — Anti-Bluff Functional Reality Mandate** — see root `/CLAUDE.md` §6.J. Every test, every Challenge Test, and every CI gate touched by this submodule MUST do exactly one job: confirm the feature it claims to cover actually works for an end user, end-to-end, on the gating matrix. CI green is necessary, never sufficient. Adding a test the author cannot execute against the gating matrix is itself a bluff. Tests must guarantee the product works — anything else is theatre.

## Clauses 6.K and 6.L (added 2026-05-04, inherited per 6.F)

- **Clause 6.K — Builds-Inside-Containers Mandate** — see root `/CLAUDE.md` §6.K. Every release-artifact build MUST run inside the project's container-bound build path (anchored on `vasic-digital/Containers`'s build orchestration: `cmd/distributed-build` + `pkg/distribution` + `pkg/runtime`), not on the developer's bare host. Local incremental dev builds on the host are permitted for iteration; the gate, the release-artifact build, and the build whose output goes through the emulator matrix (clause 6.I) MUST go through Containers. The accompanying 6.K-debt entry tracks the package additions (`pkg/emulator/`, `pkg/vm/`) that are owed.
- **Clause 6.L — Anti-Bluff Functional Reality Mandate (Operator's Standing Order)** — see root `/CLAUDE.md` §6.L. Every test, every Challenge Test, every CI gate has exactly one job: confirm the feature works for a real user end-to-end on the gating matrix. CI green is necessary, never sufficient. Tests must guarantee the product works — anything else is theatre. The operator has invoked this mandate FOUR TIMES in a single working day; the repetition itself is the forensic record. If you find yourself rationalizing a "small exception" — STOP. There are no small exceptions. The Internet Archive stuck-on-loading bug, the broken post-login navigation, the credential leak in C2, the bluffed C1-C8 — these are what "small exceptions" produce.
