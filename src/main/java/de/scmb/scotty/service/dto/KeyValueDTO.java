package de.scmb.scotty.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link de.scmb.scotty.domain.KeyValue} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class KeyValueDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String kvKey;

    @Size(max = 255)
    private String kvValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKvKey() {
        return kvKey;
    }

    public void setKvKey(String kvKey) {
        this.kvKey = kvKey;
    }

    public String getKvValue() {
        return kvValue;
    }

    public void setKvValue(String kvValue) {
        this.kvValue = kvValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KeyValueDTO)) {
            return false;
        }

        KeyValueDTO keyValueDTO = (KeyValueDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, keyValueDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "KeyValueDTO{" +
            "id=" + getId() +
            ", kvKey='" + getKvKey() + "'" +
            ", kvValue='" + getKvValue() + "'" +
            "}";
    }
}
