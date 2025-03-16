package de.scmb.scotty.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class KeyValueCriteriaTest {

    @Test
    void newKeyValueCriteriaHasAllFiltersNullTest() {
        var keyValueCriteria = new KeyValueCriteria();
        assertThat(keyValueCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void keyValueCriteriaFluentMethodsCreatesFiltersTest() {
        var keyValueCriteria = new KeyValueCriteria();

        setAllFilters(keyValueCriteria);

        assertThat(keyValueCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void keyValueCriteriaCopyCreatesNullFilterTest() {
        var keyValueCriteria = new KeyValueCriteria();
        var copy = keyValueCriteria.copy();

        assertThat(keyValueCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(keyValueCriteria)
        );
    }

    @Test
    void keyValueCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var keyValueCriteria = new KeyValueCriteria();
        setAllFilters(keyValueCriteria);

        var copy = keyValueCriteria.copy();

        assertThat(keyValueCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(keyValueCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var keyValueCriteria = new KeyValueCriteria();

        assertThat(keyValueCriteria).hasToString("KeyValueCriteria{}");
    }

    private static void setAllFilters(KeyValueCriteria keyValueCriteria) {
        keyValueCriteria.id();
        keyValueCriteria.kvKey();
        keyValueCriteria.kvValue();
        keyValueCriteria.distinct();
    }

    private static Condition<KeyValueCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getKvKey()) &&
                condition.apply(criteria.getKvValue()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<KeyValueCriteria> copyFiltersAre(KeyValueCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getKvKey(), copy.getKvKey()) &&
                condition.apply(criteria.getKvValue(), copy.getKvValue()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
