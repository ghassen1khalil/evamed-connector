package fr.has.evamed.connector.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RefProfilRefRoleId implements Serializable {

    @Column(name = "prf_code", length = 20, nullable = false)
    private String prfCode;

    @Column(name = "rol_code", length = 25, nullable = false)
    private String rolCode;

    public String getPrfCode() {
        return prfCode;
    }

    public void setPrfCode(String prfCode) {
        this.prfCode = prfCode;
    }

    public String getRolCode() {
        return rolCode;
    }

    public void setRolCode(String rolCode) {
        this.rolCode = rolCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefProfilRefRoleId that = (RefProfilRefRoleId) o;
        return Objects.equals(prfCode, that.prfCode) && Objects.equals(rolCode, that.rolCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prfCode, rolCode);
    }
}
