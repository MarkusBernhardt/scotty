package de.scmb.scotty.service.impl;

import de.scmb.scotty.domain.Reconciliation;
import de.scmb.scotty.repository.ReconciliationRepository;
import de.scmb.scotty.service.ReconciliationService;
import de.scmb.scotty.service.dto.ReconciliationDTO;
import de.scmb.scotty.service.mapper.ReconciliationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link de.scmb.scotty.domain.Reconciliation}.
 */
@Service
@Transactional
public class ReconciliationServiceImpl implements ReconciliationService {

    private static final Logger LOG = LoggerFactory.getLogger(ReconciliationServiceImpl.class);

    private final ReconciliationRepository reconciliationRepository;

    private final ReconciliationMapper reconciliationMapper;

    public ReconciliationServiceImpl(ReconciliationRepository reconciliationRepository, ReconciliationMapper reconciliationMapper) {
        this.reconciliationRepository = reconciliationRepository;
        this.reconciliationMapper = reconciliationMapper;
    }

    @Override
    public ReconciliationDTO save(ReconciliationDTO reconciliationDTO) {
        LOG.debug("Request to save Reconciliation : {}", reconciliationDTO);
        Reconciliation reconciliation = reconciliationMapper.toEntity(reconciliationDTO);
        reconciliation = reconciliationRepository.save(reconciliation);
        return reconciliationMapper.toDto(reconciliation);
    }

    @Override
    public ReconciliationDTO update(ReconciliationDTO reconciliationDTO) {
        LOG.debug("Request to update Reconciliation : {}", reconciliationDTO);
        Reconciliation reconciliation = reconciliationMapper.toEntity(reconciliationDTO);
        reconciliation = reconciliationRepository.save(reconciliation);
        return reconciliationMapper.toDto(reconciliation);
    }

    @Override
    public Optional<ReconciliationDTO> partialUpdate(ReconciliationDTO reconciliationDTO) {
        LOG.debug("Request to partially update Reconciliation : {}", reconciliationDTO);

        return reconciliationRepository
            .findById(reconciliationDTO.getId())
            .map(existingReconciliation -> {
                reconciliationMapper.partialUpdate(existingReconciliation, reconciliationDTO);

                return existingReconciliation;
            })
            .map(reconciliationRepository::save)
            .map(reconciliationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReconciliationDTO> findOne(Long id) {
        LOG.debug("Request to get Reconciliation : {}", id);
        return reconciliationRepository.findById(id).map(reconciliationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Reconciliation : {}", id);
        reconciliationRepository.deleteById(id);
    }
}
