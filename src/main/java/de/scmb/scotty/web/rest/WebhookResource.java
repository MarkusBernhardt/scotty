package de.scmb.scotty.web.rest;

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

    private final Logger log = LoggerFactory.getLogger(WebhookResource.class);

    @PostMapping("/novalnet")
    public ResponseEntity<Void> novalnet(@RequestBody String body) {
        log.error(body);
        return ResponseEntity.ok().build();
    }
}