package de.scmb.scotty.service.mapper;

import de.scmb.scotty.domain.Payment;
import de.scmb.scotty.domain.Reconciliation;
import de.scmb.scotty.service.dto.PaymentDTO;
import de.scmb.scotty.service.dto.ReconciliationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Reconciliation} and its DTO {@link Payment}.
 */
@Mapper(componentModel = "spring")
public interface PaymentReconciliationMapper extends EntityMapper<Payment, Reconciliation> {
    @Override
    @Mapping(target = "id", source = "id", ignore = true)
    @Mapping(target = "fileName", source = "fileName", ignore = true)
    @Mapping(target = "reasonCode", ignore = true)
    @Mapping(target = "scottyPayment", ignore = true)
    Reconciliation toEntity(Payment s);

    @Override
    @Mapping(target = "reconciliations", ignore = true)
    @Mapping(target = "removeReconciliation", ignore = true)
    Payment toDto(Reconciliation s);

    @Override
    @Mapping(target = "reasonCode", ignore = true)
    @Mapping(target = "scottyPayment", ignore = true)
    void partialUpdate(@MappingTarget Reconciliation entity, Payment dto);
}
