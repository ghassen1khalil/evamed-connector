package fr.has.evamed.connector.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ListePartageUtilisateurId implements Serializable {

    @Column(name = "mse_id", nullable = false)
    private Long mseId;

    @Column(name = "ut_id", nullable = false)
    private Long utId;

    public Long getMseId() {
        return mseId;
    }

    public void setMseId(Long mseId) {
        this.mseId = mseId;
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
        ListePartageUtilisateurId that = (ListePartageUtilisateurId) o;
        return Objects.equals(mseId, that.mseId) && Objects.equals(utId, that.utId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mseId, utId);
    }
}
