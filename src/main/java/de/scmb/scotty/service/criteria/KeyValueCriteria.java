package de.scmb.scotty.service.criteria;

import java.io.Serializable;
import java.util.Objects;
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
        this.id = other.id == null ? null : other.id.copy();
        this.kvKey = other.kvKey == null ? null : other.kvKey.copy();
        this.kvValue = other.kvValue == null ? null : other.kvValue.copy();
        this.distinct = other.distinct;
    }

    @Override
    public KeyValueCriteria copy() {
        return new KeyValueCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getKvKey() {
        return kvKey;
    }

    public StringFilter kvKey() {
        if (kvKey == null) {
            kvKey = new StringFilter();
        }
        return kvKey;
    }

    public void setKvKey(StringFilter kvKey) {
        this.kvKey = kvKey;
    }

    public StringFilter getKvValue() {
        return kvValue;
    }

    public StringFilter kvValue() {
        if (kvValue == null) {
            kvValue = new StringFilter();
        }
        return kvValue;
    }

    public void setKvValue(StringFilter kvValue) {
        this.kvValue = kvValue;
    }

    public Boolean getDistinct() {
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
            (id != null ? "id=" + id + ", " : "") +
            (kvKey != null ? "kvKey=" + kvKey + ", " : "") +
            (kvValue != null ? "kvValue=" + kvValue + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
