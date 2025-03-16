package de.scmb.scotty.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ReconciliationCriteriaTest {

    @Test
    void newReconciliationCriteriaHasAllFiltersNullTest() {
        var reconciliationCriteria = new ReconciliationCriteria();
        assertThat(reconciliationCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void reconciliationCriteriaFluentMethodsCreatesFiltersTest() {
        var reconciliationCriteria = new ReconciliationCriteria();

        setAllFilters(reconciliationCriteria);

        assertThat(reconciliationCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void reconciliationCriteriaCopyCreatesNullFilterTest() {
        var reconciliationCriteria = new ReconciliationCriteria();
        var copy = reconciliationCriteria.copy();

        assertThat(reconciliationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(reconciliationCriteria)
        );
    }

    @Test
    void reconciliationCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var reconciliationCriteria = new ReconciliationCriteria();
        setAllFilters(reconciliationCriteria);

        var copy = reconciliationCriteria.copy();

        assertThat(reconciliationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(reconciliationCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var reconciliationCriteria = new ReconciliationCriteria();

        assertThat(reconciliationCriteria).hasToString("ReconciliationCriteria{}");
    }

    private static void setAllFilters(ReconciliationCriteria reconciliationCriteria) {
        reconciliationCriteria.id();
        reconciliationCriteria.mandateId();
        reconciliationCriteria.paymentId();
        reconciliationCriteria.gateway();
        reconciliationCriteria.iban();
        reconciliationCriteria.bic();
        reconciliationCriteria.amount();
        reconciliationCriteria.currencyCode();
        reconciliationCriteria.softDescriptor();
        reconciliationCriteria.firstName();
        reconciliationCriteria.lastName();
        reconciliationCriteria.streetName();
        reconciliationCriteria.houseNumber();
        reconciliationCriteria.postalCode();
        reconciliationCriteria.city();
        reconciliationCriteria.countryCode();
        reconciliationCriteria.remoteIp();
        reconciliationCriteria.emailAddress();
        reconciliationCriteria.timestamp();
        reconciliationCriteria.state();
        reconciliationCriteria.reasonCode();
        reconciliationCriteria.message();
        reconciliationCriteria.gatewayId();
        reconciliationCriteria.mode();
        reconciliationCriteria.fileName();
        reconciliationCriteria.scottyPaymentId();
        reconciliationCriteria.distinct();
    }

    private static Condition<ReconciliationCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
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
                condition.apply(criteria.getReasonCode()) &&
                condition.apply(criteria.getMessage()) &&
                condition.apply(criteria.getGatewayId()) &&
                condition.apply(criteria.getMode()) &&
                condition.apply(criteria.getFileName()) &&
                condition.apply(criteria.getScottyPaymentId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ReconciliationCriteria> copyFiltersAre(
        ReconciliationCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
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
                condition.apply(criteria.getReasonCode(), copy.getReasonCode()) &&
                condition.apply(criteria.getMessage(), copy.getMessage()) &&
                condition.apply(criteria.getGatewayId(), copy.getGatewayId()) &&
                condition.apply(criteria.getMode(), copy.getMode()) &&
                condition.apply(criteria.getFileName(), copy.getFileName()) &&
                condition.apply(criteria.getScottyPaymentId(), copy.getScottyPaymentId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
