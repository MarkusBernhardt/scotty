package de.scmb.scotty.config;

import de.scmb.scotty.gateway.emerchantpay.EmerchantpayReconciliationTask;
import de.scmb.scotty.gateway.openpayd.OpenPaydService;
import java.time.ZonedDateTime;
import java.util.Objects;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@EnableScheduling
public class SchedulingConfiguration implements SchedulingConfigurer {

    private final ApplicationProperties applicationProperties;

    private final EmerchantpayReconciliationTask emerchantpayReconciliationTask;

    private final OpenPaydService openPaydService;

    public SchedulingConfiguration(
        ApplicationProperties applicationProperties,
        EmerchantpayReconciliationTask emerchantpayReconciliationTask,
        OpenPaydService openPaydService
    ) {
        this.applicationProperties = applicationProperties;
        this.emerchantpayReconciliationTask = emerchantpayReconciliationTask;
        this.openPaydService = openPaydService;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(
            emerchantpayReconciliationTask,
            triggerContext ->
                Objects
                    .requireNonNull(applicationProperties.getEmerchantpay().getReconciliationSchedule().next(ZonedDateTime.now()))
                    .toInstant()
        );
        taskRegistrar.addTriggerTask(
            new Runnable() {
                @Override
                public void run() {
                    openPaydService.execute();
                }
            },
            triggerContext -> ZonedDateTime.now().toInstant()
        );
    }
}
