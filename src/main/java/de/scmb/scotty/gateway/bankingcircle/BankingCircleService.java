package de.scmb.scotty.gateway.bankingcircle;

import de.scmb.scotty.config.ApplicationProperties;
import de.scmb.scotty.domain.Payment;
import de.scmb.scotty.gateway.openpayd.OpenPaydAccessToken;
import de.scmb.scotty.repository.PaymentRepositoryExtended;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BankingCircleService {

    private final Logger log = LoggerFactory.getLogger(BankingCircleService.class);

    private BankingCircleAccessToken bankingCircleAccessToken;

    private final ApplicationProperties applicationProperties;

    private final PaymentRepositoryExtended paymentRepositoryExtended;

    public BankingCircleService(ApplicationProperties applicationProperties, PaymentRepositoryExtended paymentRepositoryExtended) {
        this.applicationProperties = applicationProperties;
        this.paymentRepositoryExtended = paymentRepositoryExtended;
    }

    public void execute(Payment payment) {
        try {
            if (!applicationProperties.getBankingCircle().isEnabled()) {
                throw new IllegalArgumentException("BankingCircle is not enabled");
            }

            if (
                bankingCircleAccessToken == null ||
                bankingCircleAccessToken
                    .getCreatedAt()
                    .isBefore(Instant.now().minus(bankingCircleAccessToken.getExpiresIn() - 10, ChronoUnit.SECONDS))
            ) {
                loadAccessToken();
            }

            if (payment.getAmount() >= 0) {
                //executeSdd(payment);
            } else {
                throw new IllegalArgumentException("SCT is not supported for gateway BankingCircle");
            }
        } catch (Throwable t) {
            payment.setState("failed");
            payment.setMessage(t.getMessage());
            payment.setTimestamp(Instant.now());
            payment.setGatewayId("");
            payment.setMode("");
        } finally {
            paymentRepositoryExtended.save(payment);
        }
    }

    private void loadAccessToken() {
        HttpResponse<BankingCircleAccessToken> response = Unirest.get(applicationProperties.getBankingCircle().getAuthorizationUrl())
            .basicAuth(applicationProperties.getBankingCircle().getUsername(), applicationProperties.getBankingCircle().getPassword())
            .header("Content-Type", "application/json")
            .header("Charset", "utf-8")
            .header("Accept", "application/json")
            .asObject(BankingCircleAccessToken.class);

        if (response.getStatus() == 200) {
            bankingCircleAccessToken = response.getBody();
        }
    }
}
