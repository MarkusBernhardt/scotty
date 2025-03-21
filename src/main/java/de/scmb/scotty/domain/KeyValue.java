package de.scmb.scotty.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A KeyValue.
 */
@Entity
@Table(name = "key_value")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class KeyValue implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "kv_key", length = 255, nullable = false)
    private String kvKey;

    @Size(max = 255)
    @Column(name = "kv_value", length = 255)
    private String kvValue;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public KeyValue id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKvKey() {
        return this.kvKey;
    }

    public KeyValue kvKey(String kvKey) {
        this.setKvKey(kvKey);
        return this;
    }

    public void setKvKey(String kvKey) {
        this.kvKey = kvKey;
    }

    public String getKvValue() {
        return this.kvValue;
    }

    public KeyValue kvValue(String kvValue) {
        this.setKvValue(kvValue);
        return this;
    }

    public void setKvValue(String kvValue) {
        this.kvValue = kvValue;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KeyValue)) {
            return false;
        }
        return getId() != null && getId().equals(((KeyValue) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "KeyValue{" +
            "id=" + getId() +
            ", kvKey='" + getKvKey() + "'" +
            ", kvValue='" + getKvValue() + "'" +
            "}";
    }
}
