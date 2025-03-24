package de.scmb.scotty.config;

import de.scmb.scotty.domain.enumeration.Gateway;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;

/**
 * Properties specific to Scotty.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final Liquibase liquibase = new Liquibase();

    private final BankingCircle bankingCircle = new BankingCircle();

    private final Emerchantpay emerchantpay = new Emerchantpay();

    private final Novalnet novalnet = new Novalnet();

    private final OpenPayd openPayd = new OpenPayd();

    private final Proxy proxy = new Proxy();

    private final Sepa sepa = new Sepa();

    // jhipster-needle-application-properties-property

    public Liquibase getLiquibase() {
        return liquibase;
    }

    public BankingCircle getBankingCircle() {
        return this.bankingCircle;
    }

    public Emerchantpay getEmerchantpay() {
        return this.emerchantpay;
    }

    public Novalnet getNovalnet() {
        return this.novalnet;
    }

    public OpenPayd getOpenPayd() {
        return this.openPayd;
    }

    public Proxy getProxy() {
        return this.proxy;
    }

    public Sepa getSepa() {
        return this.sepa;
    }

    // jhipster-needle-application-properties-property-getter

    public static class Liquibase {

        private Boolean asyncStart;

        public Boolean getAsyncStart() {
            return asyncStart;
        }

        public void setAsyncStart(Boolean asyncStart) {
            this.asyncStart = asyncStart;
        }
    }

    public static class Emerchantpay {

        private boolean enabled;

        private String username;

        private String password;

        private String token;

        private String environment;

        private String notificationUrl;

        private String reconciliationSchedule;

        public Emerchantpay() {}

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getEnvironment() {
            return environment;
        }

        public void setEnvironment(String environment) {
            this.environment = environment;
        }

        public String getNotificationUrl() {
            return notificationUrl;
        }

        public void setNotificationUrl(String notificationUrl) {
            this.notificationUrl = notificationUrl;
        }

        public String getReconciliationSchedule() {
            return reconciliationSchedule;
        }

        public void setReconciliationSchedule(String reconciliationSchedule) {
            this.reconciliationSchedule = reconciliationSchedule;
        }
    }

    public static class Proxy {

        private String host;

        private int port;

        private String username;

        private String password;

        private String nonProxyHosts;

        public Proxy() {}

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String user) {
            this.username = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getNonProxyHosts() {
            return nonProxyHosts;
        }

        public void setNonProxyHosts(String nonProxyHosts) {
            this.nonProxyHosts = nonProxyHosts;
        }
    }

    public static class Novalnet {

        private boolean enabled;

        private String baseUrl;

        private String signature;

        private String paymentAccessKey;

        private String testMode;

        private String tariff;

        private String subscriptionTariff;

        private String webhookUrl;

        public Novalnet() {}

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public String getPaymentAccessKey() {
            return paymentAccessKey;
        }

        public void setPaymentAccessKey(String paymentAccessKey) {
            this.paymentAccessKey = paymentAccessKey;
        }

        public String getTestMode() {
            return testMode;
        }

        public void setTestMode(String testMode) {
            this.testMode = testMode;
        }

        public String getTariff() {
            return tariff;
        }

        public void setTariff(String tariff) {
            this.tariff = tariff;
        }

        public String getSubscriptionTariff() {
            return subscriptionTariff;
        }

        public void setSubscriptionTariff(String subscriptionTariff) {
            this.subscriptionTariff = subscriptionTariff;
        }

        public String getWebhookUrl() {
            return webhookUrl;
        }

        public void setWebhookUrl(String webhookUrl) {
            this.webhookUrl = webhookUrl;
        }
    }

    public static class OpenPayd {

        private boolean enabled;

        private String baseUrl;

        private String username;

        private String password;

        private String accountId;

        private String accountHolderId;

        public OpenPayd() {}

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getAccountId() {
            return accountId;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        public String getAccountHolderId() {
            return accountHolderId;
        }

        public void setAccountHolderId(String accountHolderId) {
            this.accountHolderId = accountHolderId;
        }
    }

    public static class BankingCircle {

        private boolean enabled;

        private String baseUrl;

        private String authorizationUrl;

        private String certificate;

        private String username;

        private String password;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getAuthorizationUrl() {
            return authorizationUrl;
        }

        public void setAuthorizationUrl(String authorizationUrl) {
            this.authorizationUrl = authorizationUrl;
        }

        public String getCertificate() {
            return certificate;
        }

        public void setCertificate(String certificate) {
            this.certificate = certificate;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class Sepa {

        private Gateway gateway;

        public Gateway getGateway() {
            return gateway;
        }

        public void setGateway(Gateway gateway) {
            this.gateway = gateway;
        }
    }
    // jhipster-needle-application-properties-property-class
}
