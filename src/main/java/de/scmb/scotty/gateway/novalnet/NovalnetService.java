package de.scmb.scotty.gateway.novalnet;

import static de.scmb.scotty.service.ExcelService.cutRight;

import com.emerchantpay.gateway.api.requests.financial.sdd.SDDInitRecurringSaleRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.scmb.scotty.config.ApplicationProperties;
import de.scmb.scotty.domain.Payment;
import de.scmb.scotty.repository.PaymentRepository;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NovalnetService {

    private final ApplicationProperties applicationProperties;

    private final PaymentRepository paymentRepository;

    private final RestTemplate restTemplate;

    private static final Logger log = LoggerFactory.getLogger(NovalnetService.class);

    public NovalnetService(ApplicationProperties applicationProperties, PaymentRepository paymentRepository, RestTemplateBuilder builder) {
        this.applicationProperties = applicationProperties;
        this.paymentRepository = paymentRepository;
        this.restTemplate = builder.build();
    }

    public void execute(Payment payment) {
        try {
            NovalnetPayment novalnetPayment = getNovalnetPaymentRequest(payment);

            Payment init = paymentRepository.findFirstByMandateIdAndGatewayIdNotNullAndGatewayIdNotOrderByIdAsc(payment.getMandateId(), "");
            if (init != null) {
                novalnetPayment.getTransaction().setMandateDate(init.getTimestamp().toString().substring(0, 10));
            }

            HttpResponse<NovalnetPayment> response = Unirest
                .post(applicationProperties.getNovalnet().getBaseUrl() + "/payment")
                .header(
                    "X-NN-Access-Key",
                    Base64.getEncoder().encodeToString(applicationProperties.getNovalnet().getPaymentAccessKey().getBytes())
                )
                .header("Content-Type", "application/json")
                .header("Charset", "utf-8")
                .header("Accept", "application/json")
                .body(novalnetPayment)
                .asObject(NovalnetPayment.class);

            payment.setMessage(cutRight(response.getBody().getResult().getStatusText(), 255));

            if (response.getBody().getResult().getStatusCode() == 100) {
                payment.setState("submitted");
                payment.setTimestamp(
                    Instant.parse(
                        response.getBody().getTransaction().getDate().substring(0, 10) +
                        "T" +
                        response.getBody().getTransaction().getDate().substring(11) +
                        ".000Z"
                    )
                );
                payment.setGatewayId(cutRight(response.getBody().getTransaction().getTid(), 35));
                payment.setMode(cutRight(response.getBody().getTransaction().getTestMode(), 35));
            } else {
                payment.setState("failed");
                payment.setTimestamp(Instant.now());
                payment.setGatewayId("");
                payment.setMode("");
            }
        } catch (Throwable t) {
            payment.setState("failed");
            payment.setMessage(t.getMessage());
            payment.setTimestamp(Instant.now());
            payment.setGatewayId("");
            payment.setMode("");
        } finally {
            paymentRepository.save(payment);
        }
    }

    private NovalnetPayment getNovalnetPaymentRequest(Payment payment) {
        NovalnetMerchant novalnetMerchant = new NovalnetMerchant();
        novalnetMerchant.setSignature(applicationProperties.getNovalnet().getSignature());
        novalnetMerchant.setTariff(applicationProperties.getNovalnet().getTariff());

        NovalnetAddress novalnetAddress = new NovalnetAddress();
        novalnetAddress.setStreet(payment.getStreetName());
        novalnetAddress.setHouseNo(payment.getHouseNumber());
        novalnetAddress.setZip(payment.getPostalCode());
        novalnetAddress.setCity(payment.getCity());
        novalnetAddress.setCountryCode(payment.getCountryCode());

        NovalnetCustomer novalnetCustomer = new NovalnetCustomer();
        novalnetCustomer.setFirstName(payment.getFirstName());
        novalnetCustomer.setLastName(payment.getLastName());
        novalnetCustomer.setCustomerIp(payment.getRemoteIp());
        novalnetCustomer.setEmail(payment.getEmailAddress());
        novalnetCustomer.setBilling(novalnetAddress);

        NovalnetPaymentData novalnetPaymentData = new NovalnetPaymentData();
        novalnetPaymentData.setIban(payment.getIban());
        novalnetPaymentData.setBic(payment.getBic());

        NovalnetTransaction novalnetTransaction = new NovalnetTransaction();
        novalnetTransaction.setPaymentType("DIRECT_DEBIT_SEPA");
        novalnetTransaction.setAmount(Integer.toString(payment.getAmount()));
        novalnetTransaction.setCurrency(payment.getCurrencyCode());
        novalnetTransaction.setInvoiceRef(payment.getPaymentId());
        novalnetTransaction.setMandateRef(payment.getMandateId());
        novalnetTransaction.setMandateDate(Instant.now().toString().substring(0, 10));
        novalnetTransaction.setTestMode(applicationProperties.getNovalnet().getTestMode());
        novalnetTransaction.setPaymentData(novalnetPaymentData);
        novalnetTransaction.setHookUrl(applicationProperties.getNovalnet().getWebHookUrl());
        if (payment.getSoftDescriptor().length() > 0) {
            novalnetTransaction.setDebitReason1(
                payment.getSoftDescriptor().substring(0, Math.min(27, payment.getSoftDescriptor().length()))
            );
        }
        if (payment.getSoftDescriptor().length() > 27) {
            novalnetTransaction.setDebitReason2(
                payment.getSoftDescriptor().substring(27, Math.min(54, payment.getSoftDescriptor().length()))
            );
        }
        if (payment.getSoftDescriptor().length() > 54) {
            novalnetTransaction.setDebitReason3(
                payment.getSoftDescriptor().substring(54, Math.min(81, payment.getSoftDescriptor().length()))
            );
        }
        if (payment.getSoftDescriptor().length() > 81) {
            novalnetTransaction.setDebitReason4(
                payment.getSoftDescriptor().substring(81, Math.min(108, payment.getSoftDescriptor().length()))
            );
        }
        if (payment.getSoftDescriptor().length() > 108) {
            novalnetTransaction.setDebitReason5(
                payment.getSoftDescriptor().substring(108, Math.min(135, payment.getSoftDescriptor().length()))
            );
        }

        NovalnetPayment novalnetPayment = new NovalnetPayment();
        novalnetPayment.setMerchant(novalnetMerchant);
        novalnetPayment.setCustomer(novalnetCustomer);
        novalnetPayment.setTransaction(novalnetTransaction);

        return novalnetPayment;
    }
}
