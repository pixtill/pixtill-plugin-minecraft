# Pixtill Plugin (Minecraft)

Connects a Minecraft server to a [Pixtill](https://pixtill.com) store: polls the API for paid-order
commands and runs them on the server.

- **Versions:** 1.8.8 → latest (26.2+)
- **Engines:** Bukkit (Spigot/Paper), BungeeCord (+Waterfall), Velocity
- **Design:** hexagonal (Ports & Adapters), SOLID, DRY, Value Objects — 120 unit tests, ~96% coverage

## Build

```bash
./gradlew build
```

Output: one fat JAR at `pixtill-all/build/libs/pixtill-<version>.jar`, valid on all three engines.
No tagged release is published yet — build locally until the first release.

## Docs

- [Installation](docs/installation.md) — install and first run
- [Configuration](docs/configuration.md) — `config.yml` reference
- [Development](docs/development.md) — build, run, add an engine
- [Testing](docs/testing.md) — run tests and coverage

## License

Undecided (Apache-2.0 proposed). Contributions welcome — new logic needs tests and a green build.
