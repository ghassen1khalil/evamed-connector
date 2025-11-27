package fr.has.evamed.connector.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "ma_page_utilisateur")
public class MaPageUtilisateur {

    @Id
    @Column(name = "mpa_code", length = 20)
    private String mpaCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ut_id", nullable = false)
    private Utilisateur utilisateur;

    @Column(name = "mpa_libelle", length = 50)
    private String mpaLibelle;

    @Column(name = "mpa_date_sup_log")
    private Instant mpaDateSupLog;

    @Column(name = "ordre")
    private Integer ordre;

    public String getMpaCode() {
        return mpaCode;
    }

    public void setMpaCode(String mpaCode) {
        this.mpaCode = mpaCode;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public String getMpaLibelle() {
        return mpaLibelle;
    }

    public void setMpaLibelle(String mpaLibelle) {
        this.mpaLibelle = mpaLibelle;
    }

    public Instant getMpaDateSupLog() {
        return mpaDateSupLog;
    }

    public void setMpaDateSupLog(Instant mpaDateSupLog) {
        this.mpaDateSupLog = mpaDateSupLog;
    }

    public Integer getOrdre() {
        return ordre;
    }

    public void setOrdre(Integer ordre) {
        this.ordre = ordre;
    }
}
