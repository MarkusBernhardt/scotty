package de.scmb.scotty.repository;

import de.scmb.scotty.domain.KeyValue;
import org.springframework.stereotype.Repository;

@Repository
public interface KeyValueRepositoryExtended extends KeyValueRepository {
    KeyValue findFirstByKvKeyOrderById(String kvKey);
}
