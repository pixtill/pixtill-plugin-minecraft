package pl.pixtill.plugin.port;

import pl.pixtill.plugin.domain.PlayerIdentifier;

public interface PlayerAccessor {

    boolean isOnline(PlayerIdentifier player);
}
