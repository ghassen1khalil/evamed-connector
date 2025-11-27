package fr.has.evamed.connector.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ref_profil")
public class RefProfil {

    @Id
    @Column(name = "prf_code", length = 20, nullable = false)
    private String prfCode;

    @Column(name = "prf_libelle", length = 50)
    private String prfLibelle;

    @Column(name = "prf_date_sup_log")
    private Instant prfDateSupLog;

    @OneToMany(mappedBy = "profil")
    private Set<RefProfilRefRole> profilRoles = new HashSet<>();

    public String getPrfCode() {
        return prfCode;
    }

    public void setPrfCode(String prfCode) {
        this.prfCode = prfCode;
    }

    public String getPrfLibelle() {
        return prfLibelle;
    }

    public void setPrfLibelle(String prfLibelle) {
        this.prfLibelle = prfLibelle;
    }

    public Instant getPrfDateSupLog() {
        return prfDateSupLog;
    }

    public void setPrfDateSupLog(Instant prfDateSupLog) {
        this.prfDateSupLog = prfDateSupLog;
    }

    public Set<RefProfilRefRole> getProfilRoles() {
        return profilRoles;
    }

    public void setProfilRoles(Set<RefProfilRefRole> profilRoles) {
        this.profilRoles = profilRoles;
    }
}
