package de.scmb.scotty.domain;

import static de.scmb.scotty.domain.PaymentTestSamples.*;
import static de.scmb.scotty.domain.ReconciliationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import de.scmb.scotty.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReconciliationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Reconciliation.class);
        Reconciliation reconciliation1 = getReconciliationSample1();
        Reconciliation reconciliation2 = new Reconciliation();
        assertThat(reconciliation1).isNotEqualTo(reconciliation2);

        reconciliation2.setId(reconciliation1.getId());
        assertThat(reconciliation1).isEqualTo(reconciliation2);

        reconciliation2 = getReconciliationSample2();
        assertThat(reconciliation1).isNotEqualTo(reconciliation2);
    }

    @Test
    void scottyPaymentTest() {
        Reconciliation reconciliation = getReconciliationRandomSampleGenerator();
        Payment paymentBack = getPaymentRandomSampleGenerator();

        reconciliation.setScottyPayment(paymentBack);
        assertThat(reconciliation.getScottyPayment()).isEqualTo(paymentBack);

        reconciliation.scottyPayment(null);
        assertThat(reconciliation.getScottyPayment()).isNull();
    }
}
