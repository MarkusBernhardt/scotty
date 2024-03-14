package de.scmb.scotty.task;

import com.emerchantpay.gateway.GenesisClient;
import com.emerchantpay.gateway.api.TransactionResult;
import com.emerchantpay.gateway.api.requests.nonfinancial.reconcile.ReconcileByDateRequest;
import com.emerchantpay.gateway.model.Transaction;
import com.emerchantpay.gateway.util.NodeWrapper;
import de.scmb.scotty.domain.KeyValue;
import de.scmb.scotty.repository.KeyValueRepository;
import de.scmb.scotty.service.EmerchantpayService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmerchantpayReconciliationTask implements Runnable {

    private final EmerchantpayService emerchantpayService;

    private final KeyValueRepository keyValueRepository;

    private final Logger log = LoggerFactory.getLogger(EmerchantpayReconciliationTask.class);

    public static final String LAST_READ_TIMESTAMP_KEY = "emerchantpay.reconciliation.lastReadTimestamp";

    public EmerchantpayReconciliationTask(EmerchantpayService emerchantpayService, KeyValueRepository keyValueRepository) {
        this.emerchantpayService = emerchantpayService;
        this.keyValueRepository = keyValueRepository;
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
                String status = childNode.findString("status");
                String transactionType = childNode.findString("transaction_type");
                String uniqueId = childNode.findString("unique_id");
                String transactionId = childNode.findString("transaction_id");
                int code = childNode.findInteger("code");
                String message = childNode.findString("message");
                String iban = childNode.findString("bank_account_number");
                String bic = childNode.findString("bank_identifier_code");
                String technicalMessage = childNode.findString("technical_message");
                String descriptor = childNode.findString("descriptor");
                BigDecimal amount = childNode.findBigDecimal("amount");
                String currency = childNode.findString("currency");
                String mode = childNode.findString("mode");
                String timestamp = childNode.findString("timestamp");
                boolean sentToAcquirer = childNode.findBoolean("sent_to_acquirer");
            }
        }

        //lastExecution.setValue();
        keyValueRepository.save(lastExecution);
    }
}
