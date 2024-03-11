package de.scmb.scotty.service;

import de.scmb.scotty.service.dto.KeyValueDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link de.scmb.scotty.domain.KeyValue}.
 */
public interface KeyValueService {
    /**
     * Save a keyValue.
     *
     * @param keyValueDTO the entity to save.
     * @return the persisted entity.
     */
    KeyValueDTO save(KeyValueDTO keyValueDTO);

    /**
     * Updates a keyValue.
     *
     * @param keyValueDTO the entity to update.
     * @return the persisted entity.
     */
    KeyValueDTO update(KeyValueDTO keyValueDTO);

    /**
     * Partially updates a keyValue.
     *
     * @param keyValueDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<KeyValueDTO> partialUpdate(KeyValueDTO keyValueDTO);

    /**
     * Get all the keyValues.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<KeyValueDTO> findAll(Pageable pageable);

    /**
     * Get the "id" keyValue.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<KeyValueDTO> findOne(Long id);

    /**
     * Delete the "id" keyValue.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
