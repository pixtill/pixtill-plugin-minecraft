package pl.pixtill.plugin.infrastructure.http.dto;

import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.List;

public final class CommandBatchDto {

    @SerializedName("commands")
    private List<ClaimedCommandDto> commands;

    public List<ClaimedCommandDto> getCommands() {
        return commands == null ? Collections.emptyList() : commands;
    }
}
