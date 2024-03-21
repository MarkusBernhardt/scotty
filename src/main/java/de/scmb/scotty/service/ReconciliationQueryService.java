package de.scmb.scotty.service;

import de.scmb.scotty.domain.*; // for static metamodels
import de.scmb.scotty.domain.Reconciliation;
import de.scmb.scotty.repository.ReconciliationRepository;
import de.scmb.scotty.service.criteria.ReconciliationCriteria;
import de.scmb.scotty.service.dto.ReconciliationDTO;
import de.scmb.scotty.service.mapper.ReconciliationMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Reconciliation} entities in the database.
 * The main input is a {@link ReconciliationCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ReconciliationDTO} or a {@link Page} of {@link ReconciliationDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ReconciliationQueryService extends QueryService<Reconciliation> {

    private final Logger log = LoggerFactory.getLogger(ReconciliationQueryService.class);

    private final ReconciliationRepository reconciliationRepository;

    private final ReconciliationMapper reconciliationMapper;

    public ReconciliationQueryService(ReconciliationRepository reconciliationRepository, ReconciliationMapper reconciliationMapper) {
        this.reconciliationRepository = reconciliationRepository;
        this.reconciliationMapper = reconciliationMapper;
    }

    /**
     * Return a {@link List} of {@link ReconciliationDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ReconciliationDTO> findByCriteria(ReconciliationCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Reconciliation> specification = createSpecification(criteria);
        return reconciliationMapper.toDto(reconciliationRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ReconciliationDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ReconciliationDTO> findByCriteria(ReconciliationCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Reconciliation> specification = createSpecification(criteria);
        return reconciliationRepository.findAll(specification, page).map(reconciliationMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ReconciliationCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Reconciliation> specification = createSpecification(criteria);
        return reconciliationRepository.count(specification);
    }

    /**
     * Function to convert {@link ReconciliationCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Reconciliation> createSpecification(ReconciliationCriteria criteria) {
        Specification<Reconciliation> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Reconciliation_.id));
            }
            if (criteria.getMandateId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMandateId(), Reconciliation_.mandateId));
            }
            if (criteria.getPaymentId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPaymentId(), Reconciliation_.paymentId));
            }
            if (criteria.getGateway() != null) {
                specification = specification.and(buildSpecification(criteria.getGateway(), Reconciliation_.gateway));
            }
            if (criteria.getIban() != null) {
                specification = specification.and(buildStringSpecification(criteria.getIban(), Reconciliation_.iban));
            }
            if (criteria.getBic() != null) {
                specification = specification.and(buildStringSpecification(criteria.getBic(), Reconciliation_.bic));
            }
            if (criteria.getAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAmount(), Reconciliation_.amount));
            }
            if (criteria.getCurrencyCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCurrencyCode(), Reconciliation_.currencyCode));
            }
            if (criteria.getSoftDescriptor() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSoftDescriptor(), Reconciliation_.softDescriptor));
            }
            if (criteria.getFirstName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFirstName(), Reconciliation_.firstName));
            }
            if (criteria.getLastName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastName(), Reconciliation_.lastName));
            }
            if (criteria.getAddressLine1() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddressLine1(), Reconciliation_.addressLine1));
            }
            if (criteria.getAddressLine2() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddressLine2(), Reconciliation_.addressLine2));
            }
            if (criteria.getPostalCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPostalCode(), Reconciliation_.postalCode));
            }
            if (criteria.getCity() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCity(), Reconciliation_.city));
            }
            if (criteria.getCountryCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCountryCode(), Reconciliation_.countryCode));
            }
            if (criteria.getRemoteIp() != null) {
                specification = specification.and(buildStringSpecification(criteria.getRemoteIp(), Reconciliation_.remoteIp));
            }
            if (criteria.getTimestamp() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTimestamp(), Reconciliation_.timestamp));
            }
            if (criteria.getState() != null) {
                specification = specification.and(buildStringSpecification(criteria.getState(), Reconciliation_.state));
            }
            if (criteria.getReasonCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getReasonCode(), Reconciliation_.reasonCode));
            }
            if (criteria.getMessage() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMessage(), Reconciliation_.message));
            }
            if (criteria.getGatewayId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getGatewayId(), Reconciliation_.gatewayId));
            }
            if (criteria.getMode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMode(), Reconciliation_.mode));
            }
            if (criteria.getFileName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFileName(), Reconciliation_.fileName));
            }
            if (criteria.getScottyPaymentId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getScottyPaymentId(),
                            root -> root.join(Reconciliation_.scottyPayment, JoinType.LEFT).get(Payment_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
