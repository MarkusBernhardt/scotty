package de.scmb.scotty.config;

import de.scmb.scotty.gateway.emerchantpay.EmerchantpayReconciliationTask;
import java.time.ZonedDateTime;
import java.util.Objects;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronExpression;

@Configuration
@EnableScheduling
public class SchedulingConfiguration implements SchedulingConfigurer {

    private final ApplicationProperties applicationProperties;

    private final EmerchantpayReconciliationTask emerchantpayReconciliationTask;

    public SchedulingConfiguration(
        ApplicationProperties applicationProperties,
        EmerchantpayReconciliationTask emerchantpayReconciliationTask
    ) {
        this.applicationProperties = applicationProperties;
        this.emerchantpayReconciliationTask = emerchantpayReconciliationTask;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        if(applicationProperties.getEmerchantpay().isEnabled()) {
            CronExpression cronExpression = CronExpression.parse(applicationProperties.getEmerchantpay().getReconciliationSchedule());
            taskRegistrar.addTriggerTask(
                emerchantpayReconciliationTask,
                triggerContext ->
                    Objects
                        .requireNonNull(cronExpression.next(ZonedDateTime.now()))
                        .toInstant()
            );
        }
    }
}
