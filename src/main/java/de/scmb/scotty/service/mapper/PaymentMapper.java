package de.scmb.scotty.service.mapper;

import de.scmb.scotty.domain.Payment;
import de.scmb.scotty.service.dto.PaymentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Payment} and its DTO {@link PaymentDTO}.
 */
@Mapper(componentModel = "spring")
public interface PaymentMapper extends EntityMapper<PaymentDTO, Payment> {
    @Override
    @Mapping(target = "reconciliations", ignore = true)
    @Mapping(target = "removeReconciliation", ignore = true)
    Payment toEntity(PaymentDTO p);

    @Override
    @Mapping(target = "reconciliations", ignore = true)
    @Mapping(target = "removeReconciliation", ignore = true)
    void partialUpdate(@MappingTarget Payment entity, PaymentDTO dto);
}
