package de.scmb.scotty.service;

import de.scmb.scotty.domain.*; // for static metamodels
import de.scmb.scotty.domain.Payment;
import de.scmb.scotty.repository.PaymentRepository;
import de.scmb.scotty.service.criteria.PaymentCriteria;
import de.scmb.scotty.service.dto.PaymentDTO;
import de.scmb.scotty.service.mapper.PaymentMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Payment} entities in the database.
 * The main input is a {@link PaymentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link PaymentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PaymentQueryService extends QueryService<Payment> {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentQueryService.class);

    private final PaymentRepository paymentRepository;

    private final PaymentMapper paymentMapper;

    public PaymentQueryService(PaymentRepository paymentRepository, PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
    }

    /**
     * Return a {@link Page} of {@link PaymentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PaymentDTO> findByCriteria(PaymentCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Payment> specification = createSpecification(criteria);
        return paymentRepository.findAll(specification, page).map(paymentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PaymentCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Payment> specification = createSpecification(criteria);
        return paymentRepository.count(specification);
    }

    /**
     * Function to convert {@link PaymentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Payment> createSpecification(PaymentCriteria criteria) {
        Specification<Payment> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Payment_.id));
            }
            if (criteria.getMandateId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMandateId(), Payment_.mandateId));
            }
            if (criteria.getPaymentId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPaymentId(), Payment_.paymentId));
            }
            if (criteria.getGateway() != null) {
                specification = specification.and(buildSpecification(criteria.getGateway(), Payment_.gateway));
            }
            if (criteria.getIban() != null) {
                specification = specification.and(buildStringSpecification(criteria.getIban(), Payment_.iban));
            }
            if (criteria.getBic() != null) {
                specification = specification.and(buildStringSpecification(criteria.getBic(), Payment_.bic));
            }
            if (criteria.getAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAmount(), Payment_.amount));
            }
            if (criteria.getCurrencyCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCurrencyCode(), Payment_.currencyCode));
            }
            if (criteria.getSoftDescriptor() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSoftDescriptor(), Payment_.softDescriptor));
            }
            if (criteria.getFirstName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFirstName(), Payment_.firstName));
            }
            if (criteria.getLastName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastName(), Payment_.lastName));
            }
            if (criteria.getStreetName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getStreetName(), Payment_.streetName));
            }
            if (criteria.getHouseNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getHouseNumber(), Payment_.houseNumber));
            }
            if (criteria.getPostalCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPostalCode(), Payment_.postalCode));
            }
            if (criteria.getCity() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCity(), Payment_.city));
            }
            if (criteria.getCountryCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCountryCode(), Payment_.countryCode));
            }
            if (criteria.getRemoteIp() != null) {
                specification = specification.and(buildStringSpecification(criteria.getRemoteIp(), Payment_.remoteIp));
            }
            if (criteria.getEmailAddress() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmailAddress(), Payment_.emailAddress));
            }
            if (criteria.getTimestamp() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTimestamp(), Payment_.timestamp));
            }
            if (criteria.getState() != null) {
                specification = specification.and(buildStringSpecification(criteria.getState(), Payment_.state));
            }
            if (criteria.getMessage() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMessage(), Payment_.message));
            }
            if (criteria.getGatewayId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getGatewayId(), Payment_.gatewayId));
            }
            if (criteria.getMode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMode(), Payment_.mode));
            }
            if (criteria.getFileName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFileName(), Payment_.fileName));
            }
            if (criteria.getCreditorName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreditorName(), Payment_.creditorName));
            }
            if (criteria.getCreditorIban() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreditorIban(), Payment_.creditorIban));
            }
            if (criteria.getCreditorBic() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreditorBic(), Payment_.creditorBic));
            }
            if (criteria.getCreditorId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreditorId(), Payment_.creditorId));
            }
            if (criteria.getMandateDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getMandateDate(), Payment_.mandateDate));
            }
            if (criteria.getExecutionDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getExecutionDate(), Payment_.executionDate));
            }
            if (criteria.getPaymentInformationId() != null) {
                specification = specification.and(
                    buildStringSpecification(criteria.getPaymentInformationId(), Payment_.paymentInformationId)
                );
            }
            if (criteria.getReconciliationId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getReconciliationId(), root ->
                        root.join(Payment_.reconciliations, JoinType.LEFT).get(Reconciliation_.id)
                    )
                );
            }
        }
        return specification;
    }
}
