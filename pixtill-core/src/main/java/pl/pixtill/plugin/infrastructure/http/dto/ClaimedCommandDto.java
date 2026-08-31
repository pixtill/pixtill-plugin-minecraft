package pl.pixtill.plugin.infrastructure.http.dto;

import com.google.gson.annotations.SerializedName;

public final class ClaimedCommandDto {

    @SerializedName("uuid")
    private String uuid;

    @SerializedName("command")
    private String command;

    @SerializedName("playerIdentifier")
    private String playerIdentifier;

    @SerializedName("requiresOnlinePlayer")
    private Boolean requiresOnlinePlayer;

    public String getUuid() {
        return uuid;
    }

    public String getCommand() {
        return command;
    }

    public String getPlayerIdentifier() {
        return playerIdentifier;
    }

    public boolean requiresOnlinePlayer() {
        return requiresOnlinePlayer == null || requiresOnlinePlayer;
    }
}
