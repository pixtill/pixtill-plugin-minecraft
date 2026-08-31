package pl.pixtill.plugin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ApiUrlSecurityTest {

    @Test
    void httpsIsSecure() {
        assertThat(ApiUrl.of("https://api.pixtill.com/v1").isSecure()).isTrue();
    }

    @Test
    void httpIsNotSecure() {
        assertThat(ApiUrl.of("http://api.pixtill.com/v1").isSecure()).isFalse();
    }

    @Test
    void detectsLoopbackHosts() {
        assertThat(ApiUrl.of("http://localhost:8080/v1").isLoopback()).isTrue();
        assertThat(ApiUrl.of("http://127.0.0.1:8080/v1").isLoopback()).isTrue();
        assertThat(ApiUrl.of("https://api.pixtill.com/v1").isLoopback()).isFalse();
    }
}
