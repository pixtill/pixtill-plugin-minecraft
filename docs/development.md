# Development

Build with the wrapper (no local Gradle needed):

```bash
./gradlew build              # compile + tests + coverage gate
./gradlew :pixtill-core:test # core tests only
```

Fat JAR: `pixtill-all/build/libs/pixtill-<version>.jar`. Gradle 9.1 runs on modern JDKs and
provisions the toolchains itself (Java 8 for core/Bukkit/Bungee, Java 17 for Velocity).

## Layout

```
pixtill-core/      domain + application + ports + infrastructure (no platform deps)
pixtill-bukkit/    Bukkit adapter + plugin.yml         (Java 8)
pixtill-bungee/    BungeeCord adapter + bungee.yml     (Java 8)
pixtill-velocity/  Velocity adapter                    (Java 17 — Velocity requires 17+)
pixtill-all/       aggregator -> shadow fat JAR with relocated libs
```

Ports & Adapters: `pixtill-core` depends only on its own abstractions; platform modules implement
the ports; HTTP/YAML live behind ports in `infrastructure` and are relocated in the fat JAR. The
Velocity classes are Java 17 but never load on a Java 8 Bukkit/Bungee server, so the single JAR is safe.

## Standard (enforced in review)

- **SOLID / DRY.** `domain` and `application` import no platform or library types; adapters are thin.
- **Value Objects.** Immutable, self-validating; no raw `String`/`int` in the domain. DTOs stay in `infrastructure`.
- **Tests mandatory** for new logic; coverage gate on `pixtill-core` (see [testing.md](testing.md)).
- **No comments** — names and small methods carry intent.
- Dependencies injected via `PixtillPlugin.create(...)`.

## Add an engine (OCP)

`core` does not change. Add `pixtill-<engine>`: implement the ports (`PlayerAccessor`,
`CommandDispatcher`, `TaskScheduler`, `PixtillLogger`, `PlatformDataProvider`), write an entrypoint
that calls `PixtillPlugin.create(...).onEnable()`, register `/pixtill reload`, then wire the module
into `settings.gradle.kts` and `pixtill-all`.

Never commit `config.yml` or secrets.
