package de.scmb.scotty.config;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProxyConfiguration {

    private final ApplicationProperties applicationProperties;

    private final Logger log = LoggerFactory.getLogger(ProxyConfiguration.class);

    public ProxyConfiguration(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;

        if (this.applicationProperties.getProxy().getHost() != null) {
            System.setProperty("http.proxyHost", this.applicationProperties.getProxy().getHost());
            System.setProperty("http.proxyPort", Integer.toString(this.applicationProperties.getProxy().getPort()));
            if (this.applicationProperties.getProxy().getNonProxyHosts() != null) {
                System.setProperty("http.nonProxyHosts", this.applicationProperties.getProxy().getNonProxyHosts());
            }
            System.setProperty("https.proxyHost", this.applicationProperties.getProxy().getHost());
            System.setProperty("https.proxyPort", Integer.toString(this.applicationProperties.getProxy().getPort()));
            System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");

            Authenticator.setDefault(
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        log.info("getPasswordAuthentication 1");
                        if (getRequestorType() != RequestorType.PROXY) {
                            return null;
                        }

                        String prot = getRequestingProtocol().toLowerCase();
                        if (!prot.equals("http") && !prot.equals("https")) {
                            return null;
                        }

                        if (!getRequestingHost().equalsIgnoreCase(applicationProperties.getProxy().getHost())) {
                            return null;
                        }

                        if (getRequestingPort() != applicationProperties.getProxy().getPort()) {
                            return null;
                        }

                        // Seems to be OK.
                        log.info("getPasswordAuthentication 2");
                        return new PasswordAuthentication(
                            applicationProperties.getProxy().getUsername(),
                            applicationProperties.getProxy().getPassword().toCharArray()
                        );
                    }
                }
            );
        }
    }
}
