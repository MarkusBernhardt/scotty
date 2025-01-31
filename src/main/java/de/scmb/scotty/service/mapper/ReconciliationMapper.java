package de.scmb.scotty.service.mapper;

import de.scmb.scotty.domain.Payment;
import de.scmb.scotty.domain.Reconciliation;
import de.scmb.scotty.service.dto.PaymentDTO;
import de.scmb.scotty.service.dto.ReconciliationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Reconciliation} and its DTO {@link ReconciliationDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReconciliationMapper extends EntityMapper<ReconciliationDTO, Reconciliation> {
    @Override
    @Mapping(target = "scottyPayment", source = "scottyPayment", qualifiedByName = "paymentId")
    ReconciliationDTO toDto(Reconciliation s);

    @Override
    @Mapping(target = "scottyPayment", source = "scottyPayment", qualifiedByName = "reconciliationSet")
    Reconciliation toEntity(ReconciliationDTO s);

    @Override
    @Mapping(target = "scottyPayment", source = "scottyPayment", qualifiedByName = "reconciliationSet")
    void partialUpdate(@MappingTarget Reconciliation entity, ReconciliationDTO dto);

    @Named("paymentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PaymentDTO toDtoPaymentId(Payment payment);

    @Named("reconciliationSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "reconciliations", ignore = true)
    @Mapping(target = "removeReconciliation", ignore = true)
    Payment toEntityReconciliationSet(PaymentDTO s);
}
