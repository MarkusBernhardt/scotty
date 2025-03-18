package de.scmb.scotty.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link de.scmb.scotty.domain.KeyValue} entity. This class is used
 * in {@link de.scmb.scotty.web.rest.KeyValueResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /key-values?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class KeyValueCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter kvKey;

    private StringFilter kvValue;

    private Boolean distinct;

    public KeyValueCriteria() {}

    public KeyValueCriteria(KeyValueCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.kvKey = other.optionalKvKey().map(StringFilter::copy).orElse(null);
        this.kvValue = other.optionalKvValue().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public KeyValueCriteria copy() {
        return new KeyValueCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getKvKey() {
        return kvKey;
    }

    public Optional<StringFilter> optionalKvKey() {
        return Optional.ofNullable(kvKey);
    }

    public StringFilter kvKey() {
        if (kvKey == null) {
            setKvKey(new StringFilter());
        }
        return kvKey;
    }

    public void setKvKey(StringFilter kvKey) {
        this.kvKey = kvKey;
    }

    public StringFilter getKvValue() {
        return kvValue;
    }

    public Optional<StringFilter> optionalKvValue() {
        return Optional.ofNullable(kvValue);
    }

    public StringFilter kvValue() {
        if (kvValue == null) {
            setKvValue(new StringFilter());
        }
        return kvValue;
    }

    public void setKvValue(StringFilter kvValue) {
        this.kvValue = kvValue;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final KeyValueCriteria that = (KeyValueCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(kvKey, that.kvKey) &&
            Objects.equals(kvValue, that.kvValue) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, kvKey, kvValue, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "KeyValueCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalKvKey().map(f -> "kvKey=" + f + ", ").orElse("") +
            optionalKvValue().map(f -> "kvValue=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
