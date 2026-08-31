package pl.pixtill.plugin.infrastructure.http.dto;

import com.google.gson.annotations.SerializedName;

public final class ClaimedCommandDto {

    @SerializedName("uuid")
    private String uuid;

    @SerializedName("command")
    private String command;

    @SerializedName("playerNickname")
    private String playerNickname;

    public String getUuid() {
        return uuid;
    }

    public String getCommand() {
        return command;
    }

    public String getPlayerNickname() {
        return playerNickname;
    }
}
