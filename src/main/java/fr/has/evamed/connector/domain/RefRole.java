package fr.has.evamed.connector.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ref_role")
public class RefRole {

    @Id
    @Column(name = "rol_code", length = 25, nullable = false)
    private String rolCode;

    @Column(name = "rol_libelle", length = 50)
    private String rolLibelle;

    @Column(name = "rol_ordre")
    private Integer rolOrdre;

    @Column(name = "rol_date_sup_log")
    private Instant rolDateSupLog;

    @ManyToOne
    @JoinColumn(name = "grr_code", nullable = false)
    private RefGroupeRole groupeRole;

    @OneToMany(mappedBy = "role")
    private Set<RefProfilRefRole> profilRoles = new HashSet<>();

    public String getRolCode() {
        return rolCode;
    }

    public void setRolCode(String rolCode) {
        this.rolCode = rolCode;
    }

    public String getRolLibelle() {
        return rolLibelle;
    }

    public void setRolLibelle(String rolLibelle) {
        this.rolLibelle = rolLibelle;
    }

    public Integer getRolOrdre() {
        return rolOrdre;
    }

    public void setRolOrdre(Integer rolOrdre) {
        this.rolOrdre = rolOrdre;
    }

    public Instant getRolDateSupLog() {
        return rolDateSupLog;
    }

    public void setRolDateSupLog(Instant rolDateSupLog) {
        this.rolDateSupLog = rolDateSupLog;
    }

    public RefGroupeRole getGroupeRole() {
        return groupeRole;
    }

    public void setGroupeRole(RefGroupeRole groupeRole) {
        this.groupeRole = groupeRole;
    }

    public Set<RefProfilRefRole> getProfilRoles() {
        return profilRoles;
    }

    public void setProfilRoles(Set<RefProfilRefRole> profilRoles) {
        this.profilRoles = profilRoles;
    }
}
