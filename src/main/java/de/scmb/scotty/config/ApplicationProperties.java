package de.scmb.scotty.config;

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

    // jhipster-needle-application-properties-property
    private final Emerchantpay emerchantpay = new Emerchantpay();

    private final Novalnet novalnet = new Novalnet();

    private final OpenPayd openPayd = new OpenPayd();

    private final Proxy proxy = new Proxy();

    // jhipster-needle-application-properties-property-getter
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

    // jhipster-needle-application-properties-property-class
    public static class Emerchantpay {

        private String username;

        private String password;

        private String token;

        private String environment;

        private String notificationUrl;

        private CronExpression reconciliationSchedule;

        public Emerchantpay() {}

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

        public CronExpression getReconciliationSchedule() {
            return reconciliationSchedule;
        }

        public void setReconciliationSchedule(String reconciliationSchedule) {
            this.reconciliationSchedule = CronExpression.parse(reconciliationSchedule);
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

        private String baseUrl;

        private String signature;

        private String paymentAccessKey;

        private String testMode;

        private String tariff;

        private String subscriptionTariff;

        private String webHookUrl;

        public Novalnet() {}

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

        public String getWebHookUrl() {
            return webHookUrl;
        }

        public void setWebHookUrl(String webHookUrl) {
            this.webHookUrl = webHookUrl;
        }
    }

    public static class OpenPayd {

        private String baseUrl;

        private String username;

        private String password;

        private String accountId;

        private String accountHolderId;

        public OpenPayd() {}

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
}
