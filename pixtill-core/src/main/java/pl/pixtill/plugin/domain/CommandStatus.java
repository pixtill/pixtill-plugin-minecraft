package pl.pixtill.plugin.domain;

public enum CommandStatus {

    COMPLETED("completed"),
    FAILED("failed"),
    DEFERRED("deferred");

    private final String wireValue;

    CommandStatus(final String wireValue) {
        this.wireValue = wireValue;
    }

    public String wireValue() {
        return wireValue;
    }
}
