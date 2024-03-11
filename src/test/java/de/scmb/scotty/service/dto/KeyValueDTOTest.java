package de.scmb.scotty.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import de.scmb.scotty.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class KeyValueDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(KeyValueDTO.class);
        KeyValueDTO keyValueDTO1 = new KeyValueDTO();
        keyValueDTO1.setId(1L);
        KeyValueDTO keyValueDTO2 = new KeyValueDTO();
        assertThat(keyValueDTO1).isNotEqualTo(keyValueDTO2);
        keyValueDTO2.setId(keyValueDTO1.getId());
        assertThat(keyValueDTO1).isEqualTo(keyValueDTO2);
        keyValueDTO2.setId(2L);
        assertThat(keyValueDTO1).isNotEqualTo(keyValueDTO2);
        keyValueDTO1.setId(null);
        assertThat(keyValueDTO1).isNotEqualTo(keyValueDTO2);
    }
}
