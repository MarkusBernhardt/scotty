package de.scmb.scotty.web.rest;

import de.scmb.scotty.repository.ReconciliationRepository;
import de.scmb.scotty.service.ReconciliationQueryService;
import de.scmb.scotty.service.ReconciliationService;
import de.scmb.scotty.service.criteria.ReconciliationCriteria;
import de.scmb.scotty.service.dto.ReconciliationDTO;
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
 * REST controller for managing {@link de.scmb.scotty.domain.Reconciliation}.
 */
@RestController
@RequestMapping("/api/reconciliations")
public class ReconciliationResource {

    private final Logger log = LoggerFactory.getLogger(ReconciliationResource.class);

    private static final String ENTITY_NAME = "reconciliation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReconciliationService reconciliationService;

    private final ReconciliationRepository reconciliationRepository;

    private final ReconciliationQueryService reconciliationQueryService;

    public ReconciliationResource(
        ReconciliationService reconciliationService,
        ReconciliationRepository reconciliationRepository,
        ReconciliationQueryService reconciliationQueryService
    ) {
        this.reconciliationService = reconciliationService;
        this.reconciliationRepository = reconciliationRepository;
        this.reconciliationQueryService = reconciliationQueryService;
    }

    /**
     * {@code POST  /reconciliations} : Create a new reconciliation.
     *
     * @param reconciliationDTO the reconciliationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new reconciliationDTO, or with status {@code 400 (Bad Request)} if the reconciliation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ReconciliationDTO> createReconciliation(@Valid @RequestBody ReconciliationDTO reconciliationDTO)
        throws URISyntaxException {
        log.debug("REST request to save Reconciliation : {}", reconciliationDTO);
        if (reconciliationDTO.getId() != null) {
            throw new BadRequestAlertException("A new reconciliation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ReconciliationDTO result = reconciliationService.save(reconciliationDTO);
        return ResponseEntity
            .created(new URI("/api/reconciliations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /reconciliations/:id} : Updates an existing reconciliation.
     *
     * @param id the id of the reconciliationDTO to save.
     * @param reconciliationDTO the reconciliationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reconciliationDTO,
     * or with status {@code 400 (Bad Request)} if the reconciliationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the reconciliationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReconciliationDTO> updateReconciliation(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ReconciliationDTO reconciliationDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Reconciliation : {}, {}", id, reconciliationDTO);
        if (reconciliationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reconciliationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!reconciliationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ReconciliationDTO result = reconciliationService.update(reconciliationDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, reconciliationDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /reconciliations/:id} : Partial updates given fields of an existing reconciliation, field will ignore if it is null
     *
     * @param id the id of the reconciliationDTO to save.
     * @param reconciliationDTO the reconciliationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reconciliationDTO,
     * or with status {@code 400 (Bad Request)} if the reconciliationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the reconciliationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the reconciliationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ReconciliationDTO> partialUpdateReconciliation(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ReconciliationDTO reconciliationDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Reconciliation partially : {}, {}", id, reconciliationDTO);
        if (reconciliationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reconciliationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!reconciliationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ReconciliationDTO> result = reconciliationService.partialUpdate(reconciliationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, reconciliationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /reconciliations} : get all the reconciliations.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of reconciliations in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ReconciliationDTO>> getAllReconciliations(
        ReconciliationCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Reconciliations by criteria: {}", criteria);

        Page<ReconciliationDTO> page = reconciliationQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /reconciliations/count} : count all the reconciliations.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countReconciliations(ReconciliationCriteria criteria) {
        log.debug("REST request to count Reconciliations by criteria: {}", criteria);
        return ResponseEntity.ok().body(reconciliationQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /reconciliations/:id} : get the "id" reconciliation.
     *
     * @param id the id of the reconciliationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the reconciliationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReconciliationDTO> getReconciliation(@PathVariable("id") Long id) {
        log.debug("REST request to get Reconciliation : {}", id);
        Optional<ReconciliationDTO> reconciliationDTO = reconciliationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(reconciliationDTO);
    }

    /**
     * {@code DELETE  /reconciliations/:id} : delete the "id" reconciliation.
     *
     * @param id the id of the reconciliationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReconciliation(@PathVariable("id") Long id) {
        log.debug("REST request to delete Reconciliation : {}", id);
        reconciliationService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
