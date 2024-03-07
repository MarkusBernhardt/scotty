package de.scmb.scotty.repository;

import de.scmb.scotty.domain.Payment;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Payment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findFirstByPaymentIdOrderByIdAsc(String paymentId);

    Payment findFirstByMandateIdOrderByIdAsc(String mandateId);
}
