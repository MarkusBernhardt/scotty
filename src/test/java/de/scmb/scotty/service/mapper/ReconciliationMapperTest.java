package de.scmb.scotty.service.mapper;

import org.junit.jupiter.api.BeforeEach;

class ReconciliationMapperTest {

    private ReconciliationMapper reconciliationMapper;

    @BeforeEach
    public void setUp() {
        reconciliationMapper = new ReconciliationMapperImpl();
    }
}
