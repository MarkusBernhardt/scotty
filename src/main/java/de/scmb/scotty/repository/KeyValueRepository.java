package de.scmb.scotty.repository;

import de.scmb.scotty.domain.KeyValue;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the KeyValue entity.
 */
@SuppressWarnings("unused")
@Repository
public interface KeyValueRepository extends JpaRepository<KeyValue, Long>, JpaSpecificationExecutor<KeyValue> {}
