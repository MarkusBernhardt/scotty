package de.scmb.scotty.repository;

import de.scmb.scotty.domain.Payment;
import java.util.List;
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
}
