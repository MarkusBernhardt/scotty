package de.scmb.scotty.web.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.scmb.scotty.gateway.novalnet.NovalnetPayment;
import de.scmb.scotty.gateway.novalnet.NovalnetService;
import de.scmb.scotty.gateway.openpayd.OpenPaydPayment;
import de.scmb.scotty.gateway.openpayd.OpenPaydService;
import de.scmb.scotty.gateway.openpayd.OpenPaydWebhook;
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

    private final OpenPaydService openPaydService;

    private final Logger log = LoggerFactory.getLogger(WebhookResource.class);

    public WebhookResource(NovalnetService novalnetService, OpenPaydService openPaydService) {
        this.novalnetService = novalnetService;
        this.openPaydService = openPaydService;
    }

    @PostMapping("/novalnet")
    public ResponseEntity<Void> novalnet(@RequestBody String body) {
        log.warn(body);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            NovalnetPayment novalnetPayment = objectMapper.readValue(body, NovalnetPayment.class);
            novalnetService.handleWebhook(novalnetPayment);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/openpayd")
    public ResponseEntity<Void> openpayd(@RequestBody String body) {
        log.warn(body);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            OpenPaydWebhook openPaydWebhook = objectMapper.readValue(body, OpenPaydWebhook.class);
            openPaydService.handleWebhook(openPaydWebhook);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok().build();
    }
}
