# Installation

One JAR works on every supported engine (Bukkit/Spigot/Paper, BungeeCord/Waterfall, Velocity) and
Minecraft 1.8.8 → 26.2+. No tagged release yet — build it with `./gradlew build`
(output: `pixtill-all/build/libs/pixtill-<version>.jar`).

**You need:** a store at [pixtill.com](https://pixtill.com) with your server added, plus its
**API key** and **server UUID** from the panel.

## Steps

1. Copy the JAR into the server's `plugins/` folder.
2. Start the server once — it generates `plugins/Pixtill/config.yml` and reports "requires
   configuration" (expected).
3. Set `apiKey` (panel → Settings → API keys) and `serverUuid` (panel → Servers). See
   [configuration.md](configuration.md).
4. Run `/pixtill reload` (permission `pixtill.reload`) or restart.

## Troubleshooting

| Symptom | Likely cause |
|---|---|
| still "requires configuration" | empty/placeholder `apiKey` or `serverUuid`, or a typo |
| no reaction to purchases | wrong `serverUuid` / `apiUrl`, or no internet |
| player-bound command never runs | product requires the player online; they are offline |
| auth errors in logs | invalid or revoked API key |
| interval unchanged | run `/pixtill reload` after editing `pollInterval` |

Set `debug: true` to diagnose (polling and HTTP are logged; the API key is masked), then **turn it
off** — debug logs contain sensitive data.
