package de.scmb.scotty.gateway.emerchantpay;

import static de.scmb.scotty.service.ExcelService.cutRight;

import com.emerchantpay.gateway.GenesisClient;
import com.emerchantpay.gateway.api.requests.nonfinancial.reconcile.ReconcileByDateRequest;
import com.emerchantpay.gateway.util.NodeWrapper;
import de.scmb.scotty.config.ApplicationProperties;
import de.scmb.scotty.domain.KeyValue;
import de.scmb.scotty.domain.Payment;
import de.scmb.scotty.domain.Reconciliation;
import de.scmb.scotty.domain.enumeration.Gateway;
import de.scmb.scotty.repository.KeyValueRepositoryExtended;
import de.scmb.scotty.repository.PaymentRepositoryExtended;
import de.scmb.scotty.repository.ReconciliationRepositoryExtended;
import de.scmb.scotty.service.mapper.PaymentReconciliationMapper;
import de.scmb.scotty.service.mapper.ReconciliationMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmerchantpayReconciliationTask implements Runnable {

    private final ApplicationProperties applicationProperties;

    private final EmerchantpayService emerchantpayService;

    private final KeyValueRepositoryExtended keyValueRepositoryExtended;

    private final PaymentReconciliationMapper paymentReconciliationMapper;

    private final PaymentRepositoryExtended paymentRepositoryExtended;

    private final ReconciliationMapper reconciliationMapper;

    private final ReconciliationRepositoryExtended reconciliationRepositoryExtended;

    private final Logger log = LoggerFactory.getLogger(EmerchantpayReconciliationTask.class);

    public static final String LAST_READ_TIMESTAMP_KEY = "emerchantpay.reconciliation.lastReadTimestamp";

    public EmerchantpayReconciliationTask(
        ApplicationProperties applicationProperties,
        EmerchantpayService emerchantpayService,
        KeyValueRepositoryExtended keyValueRepositoryExtended,
        PaymentReconciliationMapper paymentReconciliationMapper,
        PaymentRepositoryExtended paymentRepositoryExtended,
        ReconciliationMapper reconciliationMapper,
        ReconciliationRepositoryExtended reconciliationRepositoryExtended
    ) {
        this.applicationProperties = applicationProperties;
        this.emerchantpayService = emerchantpayService;
        this.keyValueRepositoryExtended = keyValueRepositoryExtended;
        this.paymentReconciliationMapper = paymentReconciliationMapper;
        this.paymentRepositoryExtended = paymentRepositoryExtended;
        this.reconciliationMapper = reconciliationMapper;
        this.reconciliationRepositoryExtended = reconciliationRepositoryExtended;
    }

    @Override
    public void run() {
        if (!applicationProperties.getEmerchantpay().isEnabled()) {
            return;
        }

        KeyValue lastExecution = keyValueRepositoryExtended.findFirstByKvKeyOrderById(LAST_READ_TIMESTAMP_KEY);
        if (lastExecution == null) {
            lastExecution = new KeyValue();
            lastExecution.setKvKey(LAST_READ_TIMESTAMP_KEY);
            lastExecution.setKvValue("2024-01-01T00:00:00Z");
        }

        int count;
        int page = 1;
        String startDateString = Instant.parse(lastExecution.getKvValue()).minus(60, ChronoUnit.DAYS).toString();
        startDateString = startDateString.substring(0, 10) + " " + startDateString.substring(11, 19);
        do {
            count = runReconciliationPage(startDateString, page);
            page++;
        } while (count == 100);

        Instant nowDate = Instant.now();
        String nowDateString = nowDate.toString().substring(0, 10);
        Instant importDate = Instant.parse(lastExecution.getKvValue());
        String importDateString = importDate.toString().substring(0, 10);
        while (importDateString.compareTo(nowDateString) <= 0) {
            page = 1;
            do {
                count = runChargebackPage(importDateString, page);
                page++;
            } while (count == 100);
            importDate = importDate.plus(1, ChronoUnit.DAYS);
            importDateString = importDate.toString().substring(0, 10);
        }

        lastExecution.setKvValue(Instant.now().toString());
        keyValueRepositoryExtended.save(lastExecution);
    }

    private int runReconciliationPage(String startDate, int page) {
        ReconcileByDateRequest reconcileByDateRequest = new ReconcileByDateRequest();
        reconcileByDateRequest.setStartDate(startDate);
        reconcileByDateRequest.setPage(page);

        GenesisClient client = new GenesisClient(emerchantpayService.getConfiguration(), reconcileByDateRequest);
        client.debugMode(false);
        client.execute();

        NodeWrapper nodeWrapper = client.getResponse();
        if (nodeWrapper.getElementName().equals("payment_responses")) {
            List<NodeWrapper> childNodes = nodeWrapper.getChildNodes("payment_response");
            for (NodeWrapper childNode : childNodes) {
                List<ReconciliationRepositoryExtended.StateOnly> stateOnlyList =
                    reconciliationRepositoryExtended.findAllByGatewayIdOrderById(cutRight(childNode.findString("unique_id"), 35));
                Set<String> stateOnlySet = stateOnlyList
                    .stream()
                    .map(ReconciliationRepositoryExtended.StateOnly::getState)
                    .collect(Collectors.toSet());
                Reconciliation reconciliation = getReconciliationForPaymentResponse(childNode, stateOnlySet);
                if (reconciliation == null) {
                    continue;
                }
                if (
                    (reconciliation.getState().equals("chargedBack") || reconciliation.getState().equals("refunded")) &&
                    !stateOnlySet.contains("paid")
                ) {
                    Reconciliation paid = reconciliationMapper.toEntity(reconciliationMapper.toDto(reconciliation));
                    paid.setState("paid");
                    paid.setAmount(-1 * paid.getAmount());
                    paid.setScottyPayment(reconciliation.getScottyPayment());
                    reconciliationRepositoryExtended.save(paid);
                }
                Payment payment = reconciliation.getScottyPayment();
                if (!reconciliation.getState().equals(payment.getState())) {
                    payment.setState(reconciliation.getState());
                    paymentRepositoryExtended.save(payment);
                }
                reconciliationRepositoryExtended.save(reconciliation);
            }
            return childNodes.size();
        }
        return 0;
    }

    private int runChargebackPage(String importDate, int page) {
        EmerchantpayChargebackByImportDateRequest chargebackByImportDateRequest = new EmerchantpayChargebackByImportDateRequest();
        chargebackByImportDateRequest.setImportDate(importDate);
        chargebackByImportDateRequest.setPage(page);

        GenesisClient client = new GenesisClient(emerchantpayService.getConfiguration(), chargebackByImportDateRequest);
        client.debugMode(false);
        client.execute();

        NodeWrapper nodeWrapper = client.getResponse();
        if (nodeWrapper.getElementName().equals("chargeback_responses")) {
            List<NodeWrapper> childNodes = nodeWrapper.getChildNodes("chargeback_response");
            for (NodeWrapper childNode : childNodes) {
                List<ReconciliationRepositoryExtended.StateOnly> stateOnlyList =
                    reconciliationRepositoryExtended.findAllByGatewayIdOrderById(
                        cutRight(childNode.findString("original_transaction_unique_id"), 35)
                    );
                Map<String, ReconciliationRepositoryExtended.StateOnly> stateOnlyMap = stateOnlyList
                    .stream()
                    .collect(Collectors.toMap(ReconciliationRepositoryExtended.StateOnly::getState, Function.identity()));
                Reconciliation reconciliation = getReconciliationForChargebackResponse(childNode, stateOnlyMap);
                if (reconciliation == null) {
                    continue;
                }
                if (
                    (reconciliation.getState().equals("chargedBack") || reconciliation.getState().equals("refunded")) &&
                    !stateOnlyMap.containsKey("paid")
                ) {
                    Reconciliation paid = reconciliationMapper.toEntity(reconciliationMapper.toDto(reconciliation));
                    paid.setState("paid");
                    paid.setAmount(-1 * paid.getAmount());
                    paid.setScottyPayment(reconciliation.getScottyPayment());
                    reconciliationRepositoryExtended.save(paid);
                }
                Payment payment = reconciliation.getScottyPayment();
                if (!reconciliation.getState().equals(payment.getState())) {
                    payment.setState(reconciliation.getState());
                    paymentRepositoryExtended.save(payment);
                }
                reconciliationRepositoryExtended.save(reconciliation);
            }
            return childNodes.size();
        }
        return 0;
    }

    private Reconciliation getReconciliationForPaymentResponse(NodeWrapper node, Set<String> stateOnlySet) {
        String state = EmerchantpayService.mapStateEmerchantpay(node.findString("status"));
        if (state.equals("submitted") || state.equals("pending") || stateOnlySet.contains(state)) {
            return null;
        }

        String gatewayId = node.findString("unique_id");
        Payment payment = paymentRepositoryExtended.findFirstByGatewayIdOrderByIdAsc(gatewayId);
        if (payment == null) {
            return null;
        }

        Reconciliation reconciliation = paymentReconciliationMapper.toEntity(payment);
        reconciliation.setGateway(Gateway.EMERCHANTPAY);
        reconciliation.setState(state);
        reconciliation.setScottyPayment(payment);

        reconciliation.setGatewayId(cutRight(node.findString("unique_id"), 35));
        reconciliation.setPaymentId(cutRight(node.findString("transaction_id"), 35));
        reconciliation.setMessage(cutRight(node.findString("technical_message"), 255));
        reconciliation.setIban(cutRight(node.findString("bank_account_number"), 34));
        reconciliation.setBic(cutRight(node.findString("bank_identifier_code"), 11));
        if (state.equals("paid")) {
            reconciliation.setAmount(node.findBigDecimal("amount").intValue());
        } else if (state.equals("chargedBack") || state.equals("refunded")) {
            reconciliation.setAmount(-1 * node.findBigDecimal("amount").intValue());
        } else {
            reconciliation.setAmount(0);
        }
        reconciliation.setReasonCode("");
        reconciliation.setCurrencyCode(cutRight(node.findString("currency"), 3));
        reconciliation.setMode(cutRight(node.findString("mode"), 35));
        reconciliation.setTimestamp(Instant.now());
        reconciliation.setFileName(
            "reconciliations-" + reconciliation.getTimestamp().toString().substring(0, 10).replace("-", "") + ".xlsx"
        );

        return reconciliation;
    }

    private Reconciliation getReconciliationForChargebackResponse(
        NodeWrapper node,
        Map<String, ReconciliationRepositoryExtended.StateOnly> stateOnlySet
    ) {
        String state = "chargedBack";
        ReconciliationRepositoryExtended.StateOnly stateOnly = stateOnlySet.get(state);
        if (stateOnly != null) {
            if (stateOnly.getReasonCode().isBlank()) {
                Optional<Reconciliation> optionalReconciliation = reconciliationRepositoryExtended.findById(stateOnly.getId());
                if (optionalReconciliation.isPresent()) {
                    Reconciliation reconciliation = optionalReconciliation.orElseThrow();
                    reconciliation.setReasonCode(cutRight(node.findString("reason_code"), 35));
                    reconciliationRepositoryExtended.save(reconciliation);
                }
            }
            return null;
        }

        String gatewayId = node.findString("original_transaction_unique_id");
        Payment payment = paymentRepositoryExtended.findFirstByGatewayIdOrderByIdAsc(gatewayId);
        if (payment == null) {
            return null;
        }

        Reconciliation reconciliation = paymentReconciliationMapper.toEntity(payment);
        reconciliation.setGateway(Gateway.EMERCHANTPAY);
        reconciliation.setState(state);
        reconciliation.setScottyPayment(payment);
        reconciliation.setAmount(-1 * node.findBigDecimal("chargeback_amount").intValue());
        reconciliation.setReasonCode(cutRight(node.findString("reason_code"), 35));
        reconciliation.setTimestamp(Instant.now());
        reconciliation.setFileName(
            "reconciliations-" + reconciliation.getTimestamp().toString().substring(0, 10).replace("-", "") + ".xlsx"
        );

        return reconciliation;
    }
}
