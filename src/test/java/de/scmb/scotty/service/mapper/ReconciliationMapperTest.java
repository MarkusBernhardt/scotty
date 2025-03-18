package de.scmb.scotty.service.mapper;

import static de.scmb.scotty.domain.ReconciliationAsserts.*;
import static de.scmb.scotty.domain.ReconciliationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReconciliationMapperTest {

    private ReconciliationMapper reconciliationMapper;

    @BeforeEach
    void setUp() {
        reconciliationMapper = new ReconciliationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getReconciliationSample1();
        var actual = reconciliationMapper.toEntity(reconciliationMapper.toDto(expected));
        assertReconciliationAllPropertiesEquals(expected, actual);
    }
}
