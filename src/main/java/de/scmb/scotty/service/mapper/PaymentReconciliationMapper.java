package de.scmb.scotty.service.mapper;

import de.scmb.scotty.domain.Payment;
import de.scmb.scotty.domain.Reconciliation;
import de.scmb.scotty.service.dto.PaymentDTO;
import de.scmb.scotty.service.dto.ReconciliationDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link Reconciliation} and its DTO {@link Payment}.
 */
@Mapper(componentModel = "spring")
public interface PaymentReconciliationMapper extends EntityMapper<Payment, Reconciliation> {
    @Mapping(target = "id", source = "id", ignore = true)
    @Mapping(target = "fileName", source = "fileName", ignore = true)
    Reconciliation toEntity(Payment s);
}
