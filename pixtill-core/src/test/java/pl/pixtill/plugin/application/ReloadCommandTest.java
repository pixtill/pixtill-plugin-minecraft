package pl.pixtill.plugin.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReloadCommandTest {

    private PixtillPlugin plugin;
    private ReloadCommand command;

    @BeforeEach
    void setUp() {
        plugin = mock(PixtillPlugin.class);
        command = new ReloadCommand(plugin);
    }

    @Test
    void deniesWithoutPermission() {
        final String message = command.execute(false, new String[] {"reload"});

        assertThat(message).isEqualTo(ReloadCommand.NO_PERMISSION);
        verify(plugin, never()).reload();
    }

    @Test
    void showsUsageForWrongArguments() {
        assertThat(command.execute(true, new String[] {})).isEqualTo(ReloadCommand.USAGE);
        assertThat(command.execute(true, new String[] {"foo"})).isEqualTo(ReloadCommand.USAGE);
        verify(plugin, never()).reload();
    }

    @Test
    void reloadsOnValidInvocation() {
        final String message = command.execute(true, new String[] {"reload"});

        assertThat(message).isEqualTo(ReloadCommand.RELOADED);
        verify(plugin).reload();
    }

    @Test
    void isCaseInsensitiveForSubcommand() {
        assertThat(command.execute(true, new String[] {"ReLoAd"})).isEqualTo(ReloadCommand.RELOADED);
    }
}
