# Testing

```bash
./gradlew test                      # all tests
./gradlew :pixtill-core:jacocoTestReport   # coverage report
```

Stack: JUnit 5, AssertJ, Mockito (mock ports), OkHttp MockWebServer (HTTP client without a network).

**Coverage:** 120 tests, ~96% instructions / ~88% branches on `pixtill-core`. A gate wired into
`check` fails the build below **90% instructions / 80% branches**. The untested remainder is
`equals`/`hashCode`/`toString`, an empty default no-op, and a non-deterministic filesystem-error
branch — covering those would be padding, not value.

## What is covered

- **Value objects** — validation and invariants (URL/UUID/nick/interval rules, `ApiKey`/config
  `toString` never leak the key, `ConsoleCommand` rejects control chars, `ServerVersion` parses both
  `1.8.8` and `26.2`).
- **`ProcessCommandQueueTask`** — the offline/online contract: run + confirm when eligible; **defer
  without consuming** when the required player is offline; execute once when they appear; deferral
  logged once at INFO then DEBUG; errors on fetch/dispatch/confirm are logged, never propagated.
- **HTTP client** — Hydra parsing (`member` / `hydra:member`), `X-API-Key`, PATCH confirm, malformed
  JSON, skipped-bad-record, 404/503 and network errors.
- **Config** — defaults, missing keys, non-map file, blank values, `SafeConstructor` (no YAML type
  deserialization).
- **Lifecycle** — `PixtillPluginImpl` enable/reload/disable: schedules, restarts, closes the client,
  warns on non-HTTPS.

Platform adapters are thin mappings over server APIs and are validated by smoke tests on a real
server (1.8.8 and 26.x), not unit tests.

## Guidelines

- Descriptive names (`defersCommand_whenPlayerRequiredButOffline`), mock ports not internals,
  no real network, no `sleep` (intervals are injected).
