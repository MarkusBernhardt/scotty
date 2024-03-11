package de.scmb.scotty.repository;

import de.scmb.scotty.domain.Payment;
import de.scmb.scotty.service.dto.PaymentsDownloadPaymentsDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Payment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {
    Payment findFirstByPaymentIdOrderByIdAsc(String paymentId);

    Payment findFirstByMandateIdAndGatewayIdNotNullAndGatewayIdNotOrderByIdAsc(String mandateId, String gatewayId);

    List<Payment> findAllByFileNameOrderByIdAsc(String fileName);

    @Query(
        "SELECT new de.scmb.scotty.service.dto.PaymentsDownloadPaymentsDto(p.fileName, min(p.timestamp), max(p.timestamp), COUNT(p), sum(p.amount)) FROM Payment p GROUP BY p.fileName"
    )
    Page<PaymentsDownloadPaymentsDto> findAllGroupByFileName(Pageable pageable);
}
