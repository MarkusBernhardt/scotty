package de.scmb.scotty.task;

import static de.scmb.scotty.service.ExcelService.cutRight;

import com.emerchantpay.gateway.GenesisClient;
import com.emerchantpay.gateway.api.TransactionResult;
import com.emerchantpay.gateway.api.requests.nonfinancial.reconcile.ReconcileByDateRequest;
import com.emerchantpay.gateway.model.Transaction;
import com.emerchantpay.gateway.util.NodeWrapper;
import de.scmb.scotty.domain.KeyValue;
import de.scmb.scotty.domain.Payment;
import de.scmb.scotty.domain.Reconciliation;
import de.scmb.scotty.domain.enumeration.Gateway;
import de.scmb.scotty.repository.KeyValueRepository;
import de.scmb.scotty.repository.PaymentRepository;
import de.scmb.scotty.service.EmerchantpayService;
import de.scmb.scotty.service.mapper.PaymentReconciliationMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmerchantpayReconciliationTask implements Runnable {

    private final EmerchantpayService emerchantpayService;

    private final KeyValueRepository keyValueRepository;

    private final PaymentReconciliationMapper paymentReconciliationMapper;

    private final PaymentRepository paymentRepository;

    private final Logger log = LoggerFactory.getLogger(EmerchantpayReconciliationTask.class);

    public static final String LAST_READ_TIMESTAMP_KEY = "emerchantpay.reconciliation.lastReadTimestamp";

    public EmerchantpayReconciliationTask(
        EmerchantpayService emerchantpayService,
        KeyValueRepository keyValueRepository,
        PaymentReconciliationMapper paymentReconciliationMapper,
        PaymentRepository paymentRepository
    ) {
        this.emerchantpayService = emerchantpayService;
        this.keyValueRepository = keyValueRepository;
        this.paymentReconciliationMapper = paymentReconciliationMapper;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public void run() {
        int page = 1;
        KeyValue lastExecution = keyValueRepository.findFirstByKvKeyOrderById(LAST_READ_TIMESTAMP_KEY);
        if (lastExecution == null) {
            lastExecution = new KeyValue();
            lastExecution.setKvKey(LAST_READ_TIMESTAMP_KEY);
            lastExecution.setKvValue("2024-01-01T00:00:00Z");
        }

        ReconcileByDateRequest reconcileByDateRequest = new ReconcileByDateRequest();
        reconcileByDateRequest.setStartDate(
            lastExecution.getKvValue().substring(0, 10) + " " + lastExecution.getKvValue().substring(11, 19)
        );
        reconcileByDateRequest.setPage(page);

        GenesisClient client = new GenesisClient(emerchantpayService.getConfiguration(), reconcileByDateRequest);
        client.debugMode(true);
        client.execute();

        NodeWrapper nodeWrapper = client.getResponse();
        if (nodeWrapper.getElementName().equals("payment_responses")) {
            List<NodeWrapper> childNodes = nodeWrapper.getChildNodes("payment_response");
            for (NodeWrapper childNode : childNodes) {
                String transactionType = childNode.findString("transaction_type");

                Reconciliation reconciliation = getReconciliation(childNode);
                boolean sentToAcquirer = childNode.findBoolean("sent_to_acquirer");
            }
        }

        //lastExecution.setValue();
        keyValueRepository.save(lastExecution);
    }

    private Reconciliation getReconciliation(NodeWrapper node) {
        String state = EmerchantpayService.mapStateEmerchantpay(node.findString("status"));
        if (state.equals("submitted") || state.equals("pending")) {
            return null;
        }

        String gatewayId = node.findString("unique_id");
        Payment payment = paymentRepository.findFirstByGatewayIdOrderByIdAsc(gatewayId);
        if (payment == null) {
            return null;
        }

        Reconciliation reconciliation = paymentReconciliationMapper.toEntity(payment);
        reconciliation.setGateway(Gateway.EMERCHANTPAY);
        reconciliation.setState(state);

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
        reconciliation.setCurrencyCode(cutRight(node.findString("currency"), 3));
        reconciliation.setMode(cutRight(node.findString("mode"), 35));
        reconciliation.setTimestamp(Instant.now());

        return reconciliation;
    }
}