package de.scmb.scotty.gateway.novalnet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NovalnetEvent {

    private String tid;

    @JsonProperty("parent_tid")
    private String parentTid;

    private String type;

    private String checksum;

    public NovalnetEvent() {}

    public NovalnetEvent(String tid, String parentTid, String type, String checksum) {
        this.tid = tid;
        this.parentTid = parentTid;
        this.type = type;
        this.checksum = checksum;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getParentTid() {
        return parentTid;
    }

    public void setParentTid(String parentTid) {
        this.parentTid = parentTid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NovalnetEvent that = (NovalnetEvent) o;
        return (
            Objects.equals(tid, that.tid) &&
            Objects.equals(parentTid, that.parentTid) &&
            Objects.equals(type, that.type) &&
            Objects.equals(checksum, that.checksum)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(tid, parentTid, type, checksum);
    }

    @Override
    public String toString() {
        return (
            "NovalnetEvent{" +
            "tid='" +
            tid +
            '\'' +
            ", parentTid='" +
            parentTid +
            '\'' +
            ", type='" +
            type +
            '\'' +
            ", checksum='" +
            checksum +
            '\'' +
            '}'
        );
    }
}
