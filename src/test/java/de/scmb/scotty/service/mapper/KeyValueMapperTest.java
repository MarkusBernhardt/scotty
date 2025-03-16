package de.scmb.scotty.service.mapper;

import static de.scmb.scotty.domain.KeyValueAsserts.*;
import static de.scmb.scotty.domain.KeyValueTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KeyValueMapperTest {

    private KeyValueMapper keyValueMapper;

    @BeforeEach
    void setUp() {
        keyValueMapper = new KeyValueMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getKeyValueSample1();
        var actual = keyValueMapper.toEntity(keyValueMapper.toDto(expected));
        assertKeyValueAllPropertiesEquals(expected, actual);
    }
}
