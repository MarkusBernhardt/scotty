package de.scmb.scotty.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import de.scmb.scotty.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReconciliationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReconciliationDTO.class);
        ReconciliationDTO reconciliationDTO1 = new ReconciliationDTO();
        reconciliationDTO1.setId(1L);
        ReconciliationDTO reconciliationDTO2 = new ReconciliationDTO();
        assertThat(reconciliationDTO1).isNotEqualTo(reconciliationDTO2);
        reconciliationDTO2.setId(reconciliationDTO1.getId());
        assertThat(reconciliationDTO1).isEqualTo(reconciliationDTO2);
        reconciliationDTO2.setId(2L);
        assertThat(reconciliationDTO1).isNotEqualTo(reconciliationDTO2);
        reconciliationDTO1.setId(null);
        assertThat(reconciliationDTO1).isNotEqualTo(reconciliationDTO2);
    }
}
