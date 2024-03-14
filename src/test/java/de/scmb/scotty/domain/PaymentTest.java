package de.scmb.scotty.domain;

import static de.scmb.scotty.domain.PaymentTestSamples.*;
import static de.scmb.scotty.domain.ReconciliationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import de.scmb.scotty.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PaymentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Payment.class);
        Payment payment1 = getPaymentSample1();
        Payment payment2 = new Payment();
        assertThat(payment1).isNotEqualTo(payment2);

        payment2.setId(payment1.getId());
        assertThat(payment1).isEqualTo(payment2);

        payment2 = getPaymentSample2();
        assertThat(payment1).isNotEqualTo(payment2);
    }

    @Test
    void reconciliationTest() throws Exception {
        Payment payment = getPaymentRandomSampleGenerator();
        Reconciliation reconciliationBack = getReconciliationRandomSampleGenerator();

        payment.addReconciliation(reconciliationBack);
        assertThat(payment.getReconciliations()).containsOnly(reconciliationBack);
        assertThat(reconciliationBack.getPayment()).isEqualTo(payment);

        payment.removeReconciliation(reconciliationBack);
        assertThat(payment.getReconciliations()).doesNotContain(reconciliationBack);
        assertThat(reconciliationBack.getPayment()).isNull();

        payment.reconciliations(new HashSet<>(Set.of(reconciliationBack)));
        assertThat(payment.getReconciliations()).containsOnly(reconciliationBack);
        assertThat(reconciliationBack.getPayment()).isEqualTo(payment);

        payment.setReconciliations(new HashSet<>());
        assertThat(payment.getReconciliations()).doesNotContain(reconciliationBack);
        assertThat(reconciliationBack.getPayment()).isNull();
    }
}
