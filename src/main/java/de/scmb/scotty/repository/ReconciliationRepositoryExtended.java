package de.scmb.scotty.repository;

import de.scmb.scotty.domain.Reconciliation;
import de.scmb.scotty.service.dto.PaymentsDownloadReconciliationsDto;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReconciliationRepositoryExtended extends ReconciliationRepository {
    List<StateOnly> findAllByGatewayIdOrderById(@Size(max = 35) String gatewayId);

    List<Reconciliation> findAllByFileNameOrderByIdAsc(String fileName);

    @Query(
        "SELECT new de.scmb.scotty.service.dto.PaymentsDownloadReconciliationsDto(r.fileName, COUNT(r), sum(r.amount)) FROM Reconciliation r GROUP BY r.fileName"
    )
    Page<PaymentsDownloadReconciliationsDto> findAllGroupByFileName(Pageable pageable);

    interface StateOnly {
        Long getId();
        String getState();
        String getReasonCode();
    }
}
