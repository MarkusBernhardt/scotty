package de.scmb.scotty.web.rest;

import de.scmb.scotty.gateway.novalnet.NovalnetPayment;
import de.scmb.scotty.gateway.novalnet.NovalnetService;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookResource {

    private final NovalnetService novalnetService;

    private final Logger log = LoggerFactory.getLogger(WebhookResource.class);

    public WebhookResource(NovalnetService novalnetService) {
        this.novalnetService = novalnetService;
    }

    @PostMapping("/novalnet")
    public ResponseEntity<Void> novalnet(@RequestBody NovalnetPayment payment) {
        log.warn(payment.toString());

        String tokenString = payment.getEvent().getTid() + payment.getEvent().getType() + payment.getResult().getStatus();
        if (payment.getTransaction().getAmount() != null) {
            tokenString += payment.getTransaction().getAmount();
        }
        if (payment.getTransaction().getCurrency() != null) {
            tokenString += payment.getTransaction().getCurrency();
        }

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            String checksum = new String(Hex.encode(messageDigest.digest(tokenString.getBytes(StandardCharsets.UTF_8))));
            if (!checksum.equals(payment.getEvent().getChecksum())) {
                log.error("While notifying some data has been changed. The hash check failed");
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }

        novalnetService.handleWebhook(payment);

        return ResponseEntity.ok().build();
    }
}
