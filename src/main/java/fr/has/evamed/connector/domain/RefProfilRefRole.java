package fr.has.evamed.connector.domain;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "ref_profil_ref_role")
public class RefProfilRefRole {

    @EmbeddedId
    private RefProfilRefRoleId id;

    @MapsId("prfCode")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prf_code", nullable = false)
    private RefProfil profil;

    @MapsId("rolCode")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_code", nullable = false)
    private RefRole role;

    public RefProfilRefRoleId getId() {
        return id;
    }

    public void setId(RefProfilRefRoleId id) {
        this.id = id;
    }

    public RefProfil getProfil() {
        return profil;
    }

    public void setProfil(RefProfil profil) {
        this.profil = profil;
    }

    public RefRole getRole() {
        return role;
    }

    public void setRole(RefRole role) {
        this.role = role;
    }
}
