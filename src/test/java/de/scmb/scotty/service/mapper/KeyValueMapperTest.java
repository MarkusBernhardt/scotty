package de.scmb.scotty.service.mapper;

import org.junit.jupiter.api.BeforeEach;

class KeyValueMapperTest {

    private KeyValueMapper keyValueMapper;

    @BeforeEach
    public void setUp() {
        keyValueMapper = new KeyValueMapperImpl();
    }
}
