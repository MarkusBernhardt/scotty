package de.scmb.scotty.repository;

import de.scmb.scotty.domain.Payment;
import de.scmb.scotty.domain.enumeration.Gateway;
import de.scmb.scotty.service.dto.PaymentsDownloadPaymentsDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepositoryExtended extends PaymentRepository {
    Payment findFirstByPaymentIdOrderByIdAsc(String paymentId);

    Payment findFirstByGatewayIdOrderByIdAsc(String gatewayId);

    Payment findFirstByMandateIdAndGatewayAndGatewayIdNotNullAndGatewayIdNotOrderByIdAsc(
        String mandateId,
        Gateway gateway,
        String gatewayId
    );

    List<Payment> findAllByFileNameOrderByIdAsc(String fileName);

    @Query(
        "SELECT new de.scmb.scotty.service.dto.PaymentsDownloadPaymentsDto(p.fileName, COUNT(p), sum(p.amount)) FROM Payment p GROUP BY p.fileName"
    )
    Page<PaymentsDownloadPaymentsDto> findAllGroupByFileName(Pageable pageable);
}
