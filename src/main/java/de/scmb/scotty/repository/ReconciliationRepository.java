package de.scmb.scotty.repository;

import de.scmb.scotty.domain.Reconciliation;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Reconciliation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReconciliationRepository extends JpaRepository<Reconciliation, Long>, JpaSpecificationExecutor<Reconciliation> {}
