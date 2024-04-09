package de.scmb.scotty.config;

import kong.unirest.core.Unirest;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UnirestConfiguration {

    public UnirestConfiguration(ApplicationProperties applicationProperties) {
        Unirest.config().connectTimeout(10000).enableCookieManagement(false).followRedirects(false);
    }
}
