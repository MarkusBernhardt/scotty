package de.scmb.scotty.service.mapper;

import de.scmb.scotty.domain.KeyValue;
import de.scmb.scotty.service.dto.KeyValueDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link KeyValue} and its DTO {@link KeyValueDTO}.
 */
@Mapper(componentModel = "spring")
public interface KeyValueMapper extends EntityMapper<KeyValueDTO, KeyValue> {}
