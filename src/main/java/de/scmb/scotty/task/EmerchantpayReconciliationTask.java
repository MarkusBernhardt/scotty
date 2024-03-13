package de.scmb.scotty.task;

import com.emerchantpay.gateway.GenesisClient;
import com.emerchantpay.gateway.api.TransactionResult;
import com.emerchantpay.gateway.api.requests.nonfinancial.reconcile.ReconcileByDateRequest;
import com.emerchantpay.gateway.model.Transaction;
import com.emerchantpay.gateway.util.NodeWrapper;
import de.scmb.scotty.domain.KeyValue;
import de.scmb.scotty.repository.KeyValueRepository;
import de.scmb.scotty.service.EmerchantpayService;
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

    public static final String LAST_EXECUTION_KEY = "emerchantpay.reconciliation.lastExecution";

    public EmerchantpayReconciliationTask(EmerchantpayService emerchantpayService, KeyValueRepository keyValueRepository) {
        this.emerchantpayService = emerchantpayService;
        this.keyValueRepository = keyValueRepository;
    }

    @Override
    public void run() {
        KeyValue lastExecution = keyValueRepository.findFirstByKvKeyOrderById(LAST_EXECUTION_KEY);
        if (lastExecution == null) {
            lastExecution = new KeyValue();
            lastExecution.setKvKey(LAST_EXECUTION_KEY);
            lastExecution.setKvValue("2024-01-01 00:00:00");
        }

        ReconcileByDateRequest reconcileByDateRequest = new ReconcileByDateRequest();
        reconcileByDateRequest.setStartDate(lastExecution.getKvValue());

        GenesisClient client = new GenesisClient(emerchantpayService.getConfiguration(), reconcileByDateRequest);
        client.debugMode(true);
        client.execute();

        NodeWrapper nodeWrapper = client.getResponse();
        if (nodeWrapper.getElementName().equals("payment_response")) {
            return;
        }

        List<NodeWrapper> childNodes = nodeWrapper.getChildNodes("payment_response");
        for (NodeWrapper childNode : childNodes) {
            Transaction transaction = new Transaction(childNode);
            log.debug("Transaction Id: " + transaction.getTransactionId());
            String t = transaction.getTranscationType();
        }

        //lastExecution.setValue();
        keyValueRepository.save(lastExecution);
    }
}
