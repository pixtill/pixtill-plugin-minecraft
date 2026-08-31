package pl.pixtill.plugin.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ApiUrlTest {

    @Test
    void acceptsHttpsAndStripsTrailingSlash() {
        assertThat(ApiUrl.of("https://api.example.com/v1/").value()).isEqualTo("https://api.example.com/v1");
    }

    @Test
    void acceptsHttp() {
        assertThat(ApiUrl.of("http://localhost:8080").value()).isEqualTo("http://localhost:8080");
    }

    @Test
    void resolveJoinsWithSingleSlash() {
        final ApiUrl url = ApiUrl.of("https://api.example.com/v1");
        assertThat(url.resolve("/command_executions")).isEqualTo("https://api.example.com/v1/command_executions");
        assertThat(url.resolve("command_executions")).isEqualTo("https://api.example.com/v1/command_executions");
    }

    @Test
    void rejectsEmpty() {
        assertThatThrownBy(() -> ApiUrl.of("  ")).isInstanceOf(InvalidValueException.class);
    }

    @Test
    void rejectsNonHttpScheme() {
        assertThatThrownBy(() -> ApiUrl.of("ftp://api.example.com")).isInstanceOf(InvalidValueException.class);
    }

    @Test
    void rejectsMalformed() {
        assertThatThrownBy(() -> ApiUrl.of("not a url")).isInstanceOf(InvalidValueException.class);
    }
}
