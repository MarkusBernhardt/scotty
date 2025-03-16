package de.scmb.scotty.service.impl;

import de.scmb.scotty.domain.KeyValue;
import de.scmb.scotty.repository.KeyValueRepository;
import de.scmb.scotty.service.KeyValueService;
import de.scmb.scotty.service.dto.KeyValueDTO;
import de.scmb.scotty.service.mapper.KeyValueMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link de.scmb.scotty.domain.KeyValue}.
 */
@Service
@Transactional
public class KeyValueServiceImpl implements KeyValueService {

    private static final Logger LOG = LoggerFactory.getLogger(KeyValueServiceImpl.class);

    private final KeyValueRepository keyValueRepository;

    private final KeyValueMapper keyValueMapper;

    public KeyValueServiceImpl(KeyValueRepository keyValueRepository, KeyValueMapper keyValueMapper) {
        this.keyValueRepository = keyValueRepository;
        this.keyValueMapper = keyValueMapper;
    }

    @Override
    public KeyValueDTO save(KeyValueDTO keyValueDTO) {
        LOG.debug("Request to save KeyValue : {}", keyValueDTO);
        KeyValue keyValue = keyValueMapper.toEntity(keyValueDTO);
        keyValue = keyValueRepository.save(keyValue);
        return keyValueMapper.toDto(keyValue);
    }

    @Override
    public KeyValueDTO update(KeyValueDTO keyValueDTO) {
        LOG.debug("Request to update KeyValue : {}", keyValueDTO);
        KeyValue keyValue = keyValueMapper.toEntity(keyValueDTO);
        keyValue = keyValueRepository.save(keyValue);
        return keyValueMapper.toDto(keyValue);
    }

    @Override
    public Optional<KeyValueDTO> partialUpdate(KeyValueDTO keyValueDTO) {
        LOG.debug("Request to partially update KeyValue : {}", keyValueDTO);

        return keyValueRepository
            .findById(keyValueDTO.getId())
            .map(existingKeyValue -> {
                keyValueMapper.partialUpdate(existingKeyValue, keyValueDTO);

                return existingKeyValue;
            })
            .map(keyValueRepository::save)
            .map(keyValueMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<KeyValueDTO> findOne(Long id) {
        LOG.debug("Request to get KeyValue : {}", id);
        return keyValueRepository.findById(id).map(keyValueMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete KeyValue : {}", id);
        keyValueRepository.deleteById(id);
    }
}
