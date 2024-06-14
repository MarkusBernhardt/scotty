package de.scmb.scotty.web.rest;

import de.scmb.scotty.gateway.novalnet.NovalnetPayment;
import de.scmb.scotty.gateway.novalnet.NovalnetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
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
        String tokenString = payment.getEvent().getTid() + payment.getEvent().getType() + payment.getResult().getStatus();
        if (payment.getTransaction().getAmount() != null) {
            tokenString += payment.getTransaction().getAmount();
        }
        if (payment.getTransaction().getCurrency() != null) {
            tokenString += payment.getTransaction().getCurrency();
        }

        novalnetService.handleWebhook(payment);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/openpayd")
    public ResponseEntity<Void> openpayd(@RequestBody String body) {
        log.warn(body);

        return ResponseEntity.ok().build();
    }
}
