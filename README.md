# Tracker-SDK

Generic, tracker-agnostic SDK primitives extracted from the
[Lava project](https://github.com/milos85vasic/Lava). Used to build
multi-tracker clients without coupling to any one tracker's quirks.

## Modules

- **`:api`** — generic types: `MirrorUrl`, `Protocol`, `HealthState`,
  `MirrorState`, `FallbackPolicy`, `MirrorUnavailableException`,
  `PluginConfig`, `HasId`. No tracker-specific shape.
- **`:mirror`** — `MirrorManager` + health-probe engine. Manages a list
  of mirror URLs per logical endpoint group, tracks per-mirror health,
  and provides waterfall fallback execution.
- **`:registry`** — generic plugin-registry pattern. `ConcurrentHashMap`
  of factories keyed by string ID, type-parameterized over descriptor
  and plugin types.
- **`:testing`** — fakes, fixture loaders, and the
  `falsifiabilityRehearsal` helper used to enforce Sixth Law clause 6.A
  in consuming projects.

## Constitutional inheritance

This submodule inherits all constitutional rules from the parent
[Lava project](https://github.com/milos85vasic/Lava/blob/master/CLAUDE.md):
Sixth Law (clauses 6.A–6.F), Local-Only CI/CD, Decoupled Reusable
Architecture, Host Machine Stability Directive. **Stricter rule added
here:** "no domain shape" — no class, function, file, or test resource
in this submodule may name a specific tracker, torrent site, or other
Lava-domain entity. CI gate enforces this.

## Mirroring

Mirrored to GitHub + GitLab (2 upstreams; spec-deviation 2026-04-30
reduced from original 4). See `scripts/sync-mirrors.sh`.

## Adding a primitive

See `docs/adding-a-primitive.md`.

## License

MIT (see LICENSE).
