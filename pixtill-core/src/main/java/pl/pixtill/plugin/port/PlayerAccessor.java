package pl.pixtill.plugin.port;

import pl.pixtill.plugin.domain.PlayerName;

public interface PlayerAccessor {

    boolean isOnline(PlayerName player);
}
