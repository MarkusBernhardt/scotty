package de.scmb.scotty.service;

import de.scmb.scotty.domain.*; // for static metamodels
import de.scmb.scotty.domain.KeyValue;
import de.scmb.scotty.repository.KeyValueRepository;
import de.scmb.scotty.service.criteria.KeyValueCriteria;
import de.scmb.scotty.service.dto.KeyValueDTO;
import de.scmb.scotty.service.mapper.KeyValueMapper;
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
 * Service for executing complex queries for {@link KeyValue} entities in the database.
 * The main input is a {@link KeyValueCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link KeyValueDTO} or a {@link Page} of {@link KeyValueDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class KeyValueQueryService extends QueryService<KeyValue> {

    private final Logger log = LoggerFactory.getLogger(KeyValueQueryService.class);

    private final KeyValueRepository keyValueRepository;

    private final KeyValueMapper keyValueMapper;

    public KeyValueQueryService(KeyValueRepository keyValueRepository, KeyValueMapper keyValueMapper) {
        this.keyValueRepository = keyValueRepository;
        this.keyValueMapper = keyValueMapper;
    }

    /**
     * Return a {@link List} of {@link KeyValueDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<KeyValueDTO> findByCriteria(KeyValueCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<KeyValue> specification = createSpecification(criteria);
        return keyValueMapper.toDto(keyValueRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link KeyValueDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<KeyValueDTO> findByCriteria(KeyValueCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<KeyValue> specification = createSpecification(criteria);
        return keyValueRepository.findAll(specification, page).map(keyValueMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(KeyValueCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<KeyValue> specification = createSpecification(criteria);
        return keyValueRepository.count(specification);
    }

    /**
     * Function to convert {@link KeyValueCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<KeyValue> createSpecification(KeyValueCriteria criteria) {
        Specification<KeyValue> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), KeyValue_.id));
            }
            if (criteria.getKey() != null) {
                specification = specification.and(buildStringSpecification(criteria.getKey(), KeyValue_.key));
            }
            if (criteria.getValue() != null) {
                specification = specification.and(buildStringSpecification(criteria.getValue(), KeyValue_.value));
            }
        }
        return specification;
    }
}
