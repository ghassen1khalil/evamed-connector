package fr.has.evamed.connector.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RefListeMaPageId implements Serializable {

    @Column(name = "imp_code", length = 20, nullable = false)
    private String impCode;

    @Column(name = "ut_id", nullable = false)
    private Long utId;

    public String getImpCode() {
        return impCode;
    }

    public void setImpCode(String impCode) {
        this.impCode = impCode;
    }

    public Long getUtId() {
        return utId;
    }

    public void setUtId(Long utId) {
        this.utId = utId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefListeMaPageId that = (RefListeMaPageId) o;
        return Objects.equals(impCode, that.impCode) && Objects.equals(utId, that.utId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(impCode, utId);
    }
}
