package de.scmb.scotty.domain;

import static de.scmb.scotty.domain.KeyValueTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import de.scmb.scotty.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class KeyValueTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(KeyValue.class);
        KeyValue keyValue1 = getKeyValueSample1();
        KeyValue keyValue2 = new KeyValue();
        assertThat(keyValue1).isNotEqualTo(keyValue2);

        keyValue2.setId(keyValue1.getId());
        assertThat(keyValue1).isEqualTo(keyValue2);

        keyValue2 = getKeyValueSample2();
        assertThat(keyValue1).isNotEqualTo(keyValue2);
    }
}
