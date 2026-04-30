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
