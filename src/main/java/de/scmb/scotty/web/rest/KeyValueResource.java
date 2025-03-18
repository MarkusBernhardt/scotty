package de.scmb.scotty.web.rest;

import de.scmb.scotty.repository.KeyValueRepository;
import de.scmb.scotty.service.KeyValueQueryService;
import de.scmb.scotty.service.KeyValueService;
import de.scmb.scotty.service.criteria.KeyValueCriteria;
import de.scmb.scotty.service.dto.KeyValueDTO;
import de.scmb.scotty.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link de.scmb.scotty.domain.KeyValue}.
 */
@RestController
@RequestMapping("/api/key-values")
public class KeyValueResource {

    private static final Logger LOG = LoggerFactory.getLogger(KeyValueResource.class);

    private static final String ENTITY_NAME = "keyValue";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final KeyValueService keyValueService;

    private final KeyValueRepository keyValueRepository;

    private final KeyValueQueryService keyValueQueryService;

    public KeyValueResource(
        KeyValueService keyValueService,
        KeyValueRepository keyValueRepository,
        KeyValueQueryService keyValueQueryService
    ) {
        this.keyValueService = keyValueService;
        this.keyValueRepository = keyValueRepository;
        this.keyValueQueryService = keyValueQueryService;
    }

    /**
     * {@code POST  /key-values} : Create a new keyValue.
     *
     * @param keyValueDTO the keyValueDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new keyValueDTO, or with status {@code 400 (Bad Request)} if the keyValue has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<KeyValueDTO> createKeyValue(@Valid @RequestBody KeyValueDTO keyValueDTO) throws URISyntaxException {
        LOG.debug("REST request to save KeyValue : {}", keyValueDTO);
        if (keyValueDTO.getId() != null) {
            throw new BadRequestAlertException("A new keyValue cannot already have an ID", ENTITY_NAME, "idexists");
        }
        keyValueDTO = keyValueService.save(keyValueDTO);
        return ResponseEntity.created(new URI("/api/key-values/" + keyValueDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, keyValueDTO.getId().toString()))
            .body(keyValueDTO);
    }

    /**
     * {@code PUT  /key-values/:id} : Updates an existing keyValue.
     *
     * @param id the id of the keyValueDTO to save.
     * @param keyValueDTO the keyValueDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated keyValueDTO,
     * or with status {@code 400 (Bad Request)} if the keyValueDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the keyValueDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<KeyValueDTO> updateKeyValue(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody KeyValueDTO keyValueDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update KeyValue : {}, {}", id, keyValueDTO);
        if (keyValueDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, keyValueDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!keyValueRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        keyValueDTO = keyValueService.update(keyValueDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, keyValueDTO.getId().toString()))
            .body(keyValueDTO);
    }

    /**
     * {@code PATCH  /key-values/:id} : Partial updates given fields of an existing keyValue, field will ignore if it is null
     *
     * @param id the id of the keyValueDTO to save.
     * @param keyValueDTO the keyValueDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated keyValueDTO,
     * or with status {@code 400 (Bad Request)} if the keyValueDTO is not valid,
     * or with status {@code 404 (Not Found)} if the keyValueDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the keyValueDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<KeyValueDTO> partialUpdateKeyValue(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody KeyValueDTO keyValueDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update KeyValue partially : {}, {}", id, keyValueDTO);
        if (keyValueDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, keyValueDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!keyValueRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<KeyValueDTO> result = keyValueService.partialUpdate(keyValueDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, keyValueDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /key-values} : get all the keyValues.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of keyValues in body.
     */
    @GetMapping("")
    public ResponseEntity<List<KeyValueDTO>> getAllKeyValues(
        KeyValueCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get KeyValues by criteria: {}", criteria);

        Page<KeyValueDTO> page = keyValueQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /key-values/count} : count all the keyValues.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countKeyValues(KeyValueCriteria criteria) {
        LOG.debug("REST request to count KeyValues by criteria: {}", criteria);
        return ResponseEntity.ok().body(keyValueQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /key-values/:id} : get the "id" keyValue.
     *
     * @param id the id of the keyValueDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the keyValueDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<KeyValueDTO> getKeyValue(@PathVariable("id") Long id) {
        LOG.debug("REST request to get KeyValue : {}", id);
        Optional<KeyValueDTO> keyValueDTO = keyValueService.findOne(id);
        return ResponseUtil.wrapOrNotFound(keyValueDTO);
    }

    /**
     * {@code DELETE  /key-values/:id} : delete the "id" keyValue.
     *
     * @param id the id of the keyValueDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKeyValue(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete KeyValue : {}", id);
        keyValueService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
