package de.scmb.scotty.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PaymentCriteriaTest {

    @Test
    void newPaymentCriteriaHasAllFiltersNullTest() {
        var paymentCriteria = new PaymentCriteria();
        assertThat(paymentCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void paymentCriteriaFluentMethodsCreatesFiltersTest() {
        var paymentCriteria = new PaymentCriteria();

        setAllFilters(paymentCriteria);

        assertThat(paymentCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void paymentCriteriaCopyCreatesNullFilterTest() {
        var paymentCriteria = new PaymentCriteria();
        var copy = paymentCriteria.copy();

        assertThat(paymentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(paymentCriteria)
        );
    }

    @Test
    void paymentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var paymentCriteria = new PaymentCriteria();
        setAllFilters(paymentCriteria);

        var copy = paymentCriteria.copy();

        assertThat(paymentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(paymentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var paymentCriteria = new PaymentCriteria();

        assertThat(paymentCriteria).hasToString("PaymentCriteria{}");
    }

    private static void setAllFilters(PaymentCriteria paymentCriteria) {
        paymentCriteria.id();
        paymentCriteria.mandateId();
        paymentCriteria.paymentId();
        paymentCriteria.gateway();
        paymentCriteria.iban();
        paymentCriteria.bic();
        paymentCriteria.amount();
        paymentCriteria.currencyCode();
        paymentCriteria.softDescriptor();
        paymentCriteria.firstName();
        paymentCriteria.lastName();
        paymentCriteria.streetName();
        paymentCriteria.houseNumber();
        paymentCriteria.postalCode();
        paymentCriteria.city();
        paymentCriteria.countryCode();
        paymentCriteria.remoteIp();
        paymentCriteria.emailAddress();
        paymentCriteria.timestamp();
        paymentCriteria.state();
        paymentCriteria.message();
        paymentCriteria.gatewayId();
        paymentCriteria.mode();
        paymentCriteria.fileName();
        paymentCriteria.reconciliationId();
        paymentCriteria.distinct();
    }

    private static Condition<PaymentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getMandateId()) &&
                condition.apply(criteria.getPaymentId()) &&
                condition.apply(criteria.getGateway()) &&
                condition.apply(criteria.getIban()) &&
                condition.apply(criteria.getBic()) &&
                condition.apply(criteria.getAmount()) &&
                condition.apply(criteria.getCurrencyCode()) &&
                condition.apply(criteria.getSoftDescriptor()) &&
                condition.apply(criteria.getFirstName()) &&
                condition.apply(criteria.getLastName()) &&
                condition.apply(criteria.getStreetName()) &&
                condition.apply(criteria.getHouseNumber()) &&
                condition.apply(criteria.getPostalCode()) &&
                condition.apply(criteria.getCity()) &&
                condition.apply(criteria.getCountryCode()) &&
                condition.apply(criteria.getRemoteIp()) &&
                condition.apply(criteria.getEmailAddress()) &&
                condition.apply(criteria.getTimestamp()) &&
                condition.apply(criteria.getState()) &&
                condition.apply(criteria.getMessage()) &&
                condition.apply(criteria.getGatewayId()) &&
                condition.apply(criteria.getMode()) &&
                condition.apply(criteria.getFileName()) &&
                condition.apply(criteria.getReconciliationId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PaymentCriteria> copyFiltersAre(PaymentCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getMandateId(), copy.getMandateId()) &&
                condition.apply(criteria.getPaymentId(), copy.getPaymentId()) &&
                condition.apply(criteria.getGateway(), copy.getGateway()) &&
                condition.apply(criteria.getIban(), copy.getIban()) &&
                condition.apply(criteria.getBic(), copy.getBic()) &&
                condition.apply(criteria.getAmount(), copy.getAmount()) &&
                condition.apply(criteria.getCurrencyCode(), copy.getCurrencyCode()) &&
                condition.apply(criteria.getSoftDescriptor(), copy.getSoftDescriptor()) &&
                condition.apply(criteria.getFirstName(), copy.getFirstName()) &&
                condition.apply(criteria.getLastName(), copy.getLastName()) &&
                condition.apply(criteria.getStreetName(), copy.getStreetName()) &&
                condition.apply(criteria.getHouseNumber(), copy.getHouseNumber()) &&
                condition.apply(criteria.getPostalCode(), copy.getPostalCode()) &&
                condition.apply(criteria.getCity(), copy.getCity()) &&
                condition.apply(criteria.getCountryCode(), copy.getCountryCode()) &&
                condition.apply(criteria.getRemoteIp(), copy.getRemoteIp()) &&
                condition.apply(criteria.getEmailAddress(), copy.getEmailAddress()) &&
                condition.apply(criteria.getTimestamp(), copy.getTimestamp()) &&
                condition.apply(criteria.getState(), copy.getState()) &&
                condition.apply(criteria.getMessage(), copy.getMessage()) &&
                condition.apply(criteria.getGatewayId(), copy.getGatewayId()) &&
                condition.apply(criteria.getMode(), copy.getMode()) &&
                condition.apply(criteria.getFileName(), copy.getFileName()) &&
                condition.apply(criteria.getReconciliationId(), copy.getReconciliationId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
