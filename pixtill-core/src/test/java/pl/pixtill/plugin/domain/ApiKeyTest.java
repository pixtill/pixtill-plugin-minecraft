package pl.pixtill.plugin.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ApiKeyTest {

    @Test
    void exposesRawValue() {
        assertThat(ApiKey.of("pk_live_secret123").value()).isEqualTo("pk_live_secret123");
    }

    @Test
    void toStringIsMaskedAndHidesSecret() {
        final ApiKey key = ApiKey.of("pk_live_secret123");
        assertThat(key.toString()).isEqualTo(key.masked());
        assertThat(key.toString()).doesNotContain("secret123");
        assertThat(key.toString()).startsWith("pk_l");
    }

    @Test
    void masksShortKeyEntirely() {
        assertThat(ApiKey.of("abc").masked()).isEqualTo("••••");
    }

    @Test
    void rejectsEmpty() {
        assertThatThrownBy(() -> ApiKey.of("")).isInstanceOf(InvalidValueException.class);
    }

    @Test
    void equalityByValue() {
        assertThat(ApiKey.of("same")).isEqualTo(ApiKey.of("same"));
        assertThat(ApiKey.of("a")).isNotEqualTo(ApiKey.of("b"));
    }
}
