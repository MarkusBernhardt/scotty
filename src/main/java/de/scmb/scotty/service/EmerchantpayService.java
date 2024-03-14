package de.scmb.scotty.service;

import static de.scmb.scotty.service.ExcelService.cutRight;

import com.emerchantpay.gateway.GenesisClient;
import com.emerchantpay.gateway.api.Request;
import com.emerchantpay.gateway.api.TransactionResult;
import com.emerchantpay.gateway.api.constants.Endpoints;
import com.emerchantpay.gateway.api.constants.Environments;
import com.emerchantpay.gateway.api.requests.financial.sdd.SDDInitRecurringSaleRequest;
import com.emerchantpay.gateway.api.requests.financial.sdd.SDDRecurringSaleRequest;
import com.emerchantpay.gateway.model.Transaction;
import com.emerchantpay.gateway.util.Configuration;
import de.scmb.scotty.config.ApplicationProperties;
import de.scmb.scotty.domain.Payment;
import de.scmb.scotty.repository.PaymentRepository;
import java.math.BigDecimal;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmerchantpayService {

    private final ApplicationProperties applicationProperties;

    private final PaymentRepository paymentRepository;

    private final Logger log = LoggerFactory.getLogger(EmerchantpayService.class);

    public EmerchantpayService(ApplicationProperties applicationProperties, PaymentRepository paymentRepository) {
        this.applicationProperties = applicationProperties;
        this.paymentRepository = paymentRepository;
    }

    public boolean execute(Payment payment) {
        try {
            Request request;
            Payment init = paymentRepository.findFirstByMandateIdAndGatewayIdNotNullAndGatewayIdNotOrderByIdAsc(payment.getMandateId(), "");
            if (init == null) {
                request = getSddInitRecurringSaleRequest(payment);
            } else {
                request = getSddRecurringSaleRequest(payment, init);
                payment.setFirstName(init.getFirstName());
                payment.setLastName(init.getLastName());
                payment.setCity(init.getCity());
                payment.setPostalCode(init.getPostalCode());
                payment.setAddressLine1(init.getAddressLine1());
                payment.setAddressLine2(init.getAddressLine2());
                payment.setCountryCode(init.getCountryCode());
                payment.setIban(init.getIban());
                payment.setBic(init.getBic());
            }

            Payment duplicate = paymentRepository.findFirstByPaymentIdOrderByIdAsc(payment.getPaymentId());
            if (duplicate != null) {
                payment.setState("failed");
                payment.setMessage("Duplicate payment with id: " + duplicate.getId());
                payment.setTimestamp(Instant.now());
                payment.setGatewayId("");
                payment.setMode("");
                // TODO
                // RECON
                return true;
            }

            GenesisClient client = new GenesisClient(getConfiguration(), request);
            client.debugMode(true);
            client.execute();

            // Parse Payment result
            TransactionResult<? extends Transaction> result = client.getTransaction().getRequest();
            log.debug("Transaction Id: " + result.getTransaction().getTransactionId());

            payment.setMessage(cutRight(result.getTransaction().getMessage(), 255));
            payment.setGatewayId(cutRight(result.getTransaction().getUniqueId(), 35));
            payment.setMode(cutRight(result.getTransaction().getMode(), 35));
            payment.setState(cutRight(mapStateEmerchantpay(result.getTransaction().getStatus()), 35));
            payment.setTimestamp(Instant.parse(result.getTransaction().getTimestamp()));
        } catch (Throwable t) {
            payment.setState("failed");
            payment.setMessage(t.getMessage());
            payment.setTimestamp(Instant.now());
            payment.setGatewayId("");
            payment.setMode("");
            return true;
        } finally {
            paymentRepository.save(payment);
        }
        return false;
    }

    private SDDRecurringSaleRequest getSddRecurringSaleRequest(Payment payment, Payment init) {
        SDDRecurringSaleRequest sddRecurringSaleRequest = new SDDRecurringSaleRequest();
        sddRecurringSaleRequest.setAmount(BigDecimal.valueOf(payment.getAmount() / 100d));
        sddRecurringSaleRequest.setCurrency(payment.getCurrencyCode());
        sddRecurringSaleRequest.setTransactionId(payment.getPaymentId());
        sddRecurringSaleRequest.setUsage(payment.getSoftDescriptor());
        sddRecurringSaleRequest.setRemoteIp(payment.getRemoteIp());
        sddRecurringSaleRequest.setReferenceId(init.getGatewayId());

        return sddRecurringSaleRequest;
    }

    private SDDInitRecurringSaleRequest getSddInitRecurringSaleRequest(Payment payment) {
        SDDInitRecurringSaleRequest sddInitRecurringSaleRequest = new SDDInitRecurringSaleRequest();
        sddInitRecurringSaleRequest.setAmount(BigDecimal.valueOf(payment.getAmount() / 100d));
        sddInitRecurringSaleRequest.setCurrency(payment.getCurrencyCode());
        sddInitRecurringSaleRequest.setBillingFirstname(payment.getFirstName());
        sddInitRecurringSaleRequest.setBillingLastname(payment.getLastName());
        sddInitRecurringSaleRequest.setBillingCity(payment.getCity());
        sddInitRecurringSaleRequest.setBillingZipCode(payment.getPostalCode());
        sddInitRecurringSaleRequest.setBillingPrimaryAddress(payment.getAddressLine1());
        sddInitRecurringSaleRequest.setBillingSecondaryAddress(payment.getAddressLine2());
        sddInitRecurringSaleRequest.setBillingCountry(payment.getCountryCode());
        sddInitRecurringSaleRequest.setIban(payment.getIban());
        sddInitRecurringSaleRequest.setBic(payment.getBic());
        sddInitRecurringSaleRequest.setTransactionId(payment.getPaymentId());
        sddInitRecurringSaleRequest.setUsage(payment.getSoftDescriptor());
        sddInitRecurringSaleRequest.setRemoteIp(payment.getRemoteIp());

        return sddInitRecurringSaleRequest;
    }

    public static String mapStateEmerchantpay(String state) {
        return switch (state) {
            case "approved" -> "paid";
            case "pending_async" -> "submitted";
            case "pending_hold", "pending_review", "pending" -> "pending";
            case "refunded" -> "refunded";
            case "chargebacked" -> "chargedBack";
            default -> "failed";
        };
    }

    public Configuration getConfiguration() {
        Environments environment = Environments.STAGING;
        if (applicationProperties.getEmerchantpay().getEnvironment().equals("production")) {
            environment = Environments.PRODUCTION;
        }

        Configuration configuration = new Configuration(environment, Endpoints.EMERCHANTPAY);
        configuration.setUsername(applicationProperties.getEmerchantpay().getUsername());
        configuration.setPassword(applicationProperties.getEmerchantpay().getPassword());
        configuration.setToken(applicationProperties.getEmerchantpay().getToken());

        return configuration;
    }
}
