package de.scmb.scotty.service;

import de.scmb.scotty.config.ApplicationProperties;
import de.scmb.scotty.domain.Payment;
import de.scmb.scotty.repository.PaymentRepository;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class NovalnetService {

    private final ApplicationProperties applicationProperties;

    private final PaymentRepository paymentRepository;

    public NovalnetService(ApplicationProperties applicationProperties, PaymentRepository paymentRepository) {
        this.applicationProperties = applicationProperties;
        this.paymentRepository = paymentRepository;
    }

    public void execute(Payment payment) {
        try {} catch (Throwable t) {
            payment.setState("failed");
            payment.setMessage(t.getMessage());
            payment.setTimestamp(Instant.now());
            payment.setGatewayId("");
            payment.setMode("");
        } finally {
            paymentRepository.save(payment);
        }
    }
}
