package de.scmb.scotty.gateway.novalnet;

import static de.scmb.scotty.service.ExcelService.cutRight;

import com.emerchantpay.gateway.api.requests.financial.sdd.SDDInitRecurringSaleRequest;
import com.emerchantpay.gateway.util.NodeWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.scmb.scotty.config.ApplicationProperties;
import de.scmb.scotty.domain.Payment;
import de.scmb.scotty.domain.Reconciliation;
import de.scmb.scotty.domain.enumeration.Gateway;
import de.scmb.scotty.gateway.emerchantpay.EmerchantpayService;
import de.scmb.scotty.repository.PaymentRepository;
import de.scmb.scotty.repository.ReconciliationRepository;
import de.scmb.scotty.service.mapper.PaymentReconciliationMapper;
import de.scmb.scotty.service.mapper.ReconciliationMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
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

    private final PaymentReconciliationMapper paymentReconciliationMapper;

    private final ReconciliationMapper reconciliationMapper;

    private final ReconciliationRepository reconciliationRepository;

    private static final Logger log = LoggerFactory.getLogger(NovalnetService.class);

    public NovalnetService(
        ApplicationProperties applicationProperties,
        PaymentRepository paymentRepository,
        PaymentReconciliationMapper paymentReconciliationMapper,
        ReconciliationMapper reconciliationMapper,
        ReconciliationRepository reconciliationRepository
    ) {
        this.applicationProperties = applicationProperties;
        this.paymentRepository = paymentRepository;
        this.paymentReconciliationMapper = paymentReconciliationMapper;
        this.reconciliationMapper = reconciliationMapper;
        this.reconciliationRepository = reconciliationRepository;
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

    public void handleWebhook(NovalnetPayment novalnetPayment) {
        if (!novalnetPayment.getEvent().getType().equals("CHARGEBACK")) {
            return;
        }

        List<ReconciliationRepository.StateOnly> stateOnlyList = reconciliationRepository.findAllByGatewayIdOrderById(
            novalnetPayment.getEvent().getParentTid()
        );
        Set<String> stateOnlySet = stateOnlyList.stream().map(ReconciliationRepository.StateOnly::getState).collect(Collectors.toSet());

        Reconciliation reconciliation = getReconciliationForPaymentResponse(novalnetPayment, stateOnlySet);
        if (reconciliation == null) {
            return;
        }

        if (
            (reconciliation.getState().equals("chargedBack") || reconciliation.getState().equals("refunded")) &&
            !stateOnlySet.contains("paid")
        ) {
            Reconciliation paid = reconciliationMapper.toEntity(reconciliationMapper.toDto(reconciliation));
            paid.setState("paid");
            paid.setAmount(-1 * paid.getAmount());
            paid.setReasonCode("");
            paid.setMessage(reconciliation.getScottyPayment().getMessage());
            paid.setScottyPayment(reconciliation.getScottyPayment());
            reconciliationRepository.save(paid);
        }
        Payment payment = reconciliation.getScottyPayment();
        if (!reconciliation.getState().equals(payment.getState())) {
            payment.setState(reconciliation.getState());
            paymentRepository.save(payment);
        }
        reconciliationRepository.save(reconciliation);
    }

    private Reconciliation getReconciliationForPaymentResponse(NovalnetPayment novalnetPayment, Set<String> stateOnlySet) {
        String state = mapStateNovalnet(novalnetPayment.getEvent().getType());
        if (state.equals("unknown") || stateOnlySet.contains(state)) {
            return null;
        }

        String gatewayId = novalnetPayment.getEvent().getParentTid();
        Payment payment = paymentRepository.findFirstByGatewayIdOrderByIdAsc(gatewayId);
        if (payment == null) {
            return null;
        }

        Reconciliation reconciliation = paymentReconciliationMapper.toEntity(payment);

        reconciliation.setGateway(Gateway.NOVALNET);
        reconciliation.setState(state);
        reconciliation.setScottyPayment(payment);

        if (state.equals("chargedBack")) {
            reconciliation.setAmount(-1 * Integer.parseInt(novalnetPayment.getTransaction().getAmount()));
            reconciliation.setReasonCode(cutRight(novalnetPayment.getTransaction().getReasonCode(), 35));
            reconciliation.setMessage(novalnetPayment.getTransaction().getReason());
        }

        reconciliation.setTimestamp(Instant.now());
        reconciliation.setFileName(
            "reconciliations-" + reconciliation.getTimestamp().toString().substring(0, 10).replace("-", "") + ".xlsx"
        );

        return reconciliation;
    }

    private static String mapStateNovalnet(String state) {
        return switch (state) {
            case "PAYMENT" -> "paid";
            case "CHARGEBACK" -> "chargedBack";
            default -> "unknown";
        };
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
