package de.scmb.scotty.task;

import com.emerchantpay.gateway.GenesisClient;
import com.emerchantpay.gateway.api.requests.nonfinancial.reconcile.ReconcileByDateRequest;
import com.emerchantpay.gateway.util.Configuration;
import de.scmb.scotty.domain.KeyValue;
import de.scmb.scotty.repository.KeyValueRepository;
import de.scmb.scotty.service.EmerchantpayService;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class EmerchantpayReconciliationTask implements Runnable {

    private final EmerchantpayService emerchantpayService;

    private final KeyValueRepository keyValueRepository;

    public static final String LAST_EXECUTION_KEY = "emerchantpay.reconciliation.lastExecution";

    public EmerchantpayReconciliationTask(EmerchantpayService emerchantpayService, KeyValueRepository keyValueRepository) {
        this.emerchantpayService = emerchantpayService;
        this.keyValueRepository = keyValueRepository;
    }

    @Override
    public void run() {
        KeyValue lastExecution = keyValueRepository.findFirstByKeyOrderByIdAsc(LAST_EXECUTION_KEY);

        ReconcileByDateRequest reconcileByDateRequest = new ReconcileByDateRequest();
        if (lastExecution != null) {
            reconcileByDateRequest.setStartDate(lastExecution.getValue());
        }

        GenesisClient client = new GenesisClient(emerchantpayService.getConfiguration(), reconcileByDateRequest);
        client.debugMode(true);
        client.execute();

        //lastExecution.setValue();
        keyValueRepository.save(lastExecution);
    }
}
