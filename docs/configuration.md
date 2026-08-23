# Configuration

`plugins/Pixtill/config.yml`, generated on first run:

```yaml
apiUrl: "https://api.pixtill.com/v1"  # leave default unless self-hosting
apiKey: ""                            # panel -> Settings -> API keys (X-API-Key header)
serverUuid: ""                        # panel -> Servers
pollInterval: "30s"                   # 30s, 1m, 2m30s... minimum 5s
debug: false                          # verbose HTTP logs; sensitive; diagnosis only
```

| Field | Required | Notes |
|---|---|---|
| `apiUrl` | no | Base API URL; normalized (no trailing `/`). **Use HTTPS in production** — over HTTP the key and commands travel in plaintext (the plugin warns at startup). |
| `apiKey` | **yes** | Authenticates the plugin. Treat like a password; masked in logs. Empty ⇒ polling does not start. |
| `serverUuid` | **yes** | Which server's commands to fetch. Wrong value ⇒ no orders run. |
| `pollInterval` | no | Freshness vs. load; `30s`–`60s` recommended. Below 5s is raised to 5s. Changing it needs `/pixtill reload`. |
| `debug` | no | Logs requests/responses (key masked). Turn off after diagnosis. |

`/pixtill reload` (permission `pixtill.reload`) re-reads the file and restarts polling with the new
interval; a server restart does the same.

**Security:** the key grants full store access — protect `config.yml`, never commit it, keep
`debug: false` in production, and rotate the key if it leaks.
