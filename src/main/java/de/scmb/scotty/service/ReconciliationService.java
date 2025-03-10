package de.scmb.scotty.service;

import de.scmb.scotty.service.dto.ReconciliationDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link de.scmb.scotty.domain.Reconciliation}.
 */
public interface ReconciliationService {
    /**
     * Save a reconciliation.
     *
     * @param reconciliationDTO the entity to save.
     * @return the persisted entity.
     */
    ReconciliationDTO save(ReconciliationDTO reconciliationDTO);

    /**
     * Updates a reconciliation.
     *
     * @param reconciliationDTO the entity to update.
     * @return the persisted entity.
     */
    ReconciliationDTO update(ReconciliationDTO reconciliationDTO);

    /**
     * Partially updates a reconciliation.
     *
     * @param reconciliationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ReconciliationDTO> partialUpdate(ReconciliationDTO reconciliationDTO);

    /**
     * Get all the reconciliations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ReconciliationDTO> findAll(Pageable pageable);

    /**
     * Get the "id" reconciliation.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ReconciliationDTO> findOne(Long id);

    /**
     * Delete the "id" reconciliation.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
