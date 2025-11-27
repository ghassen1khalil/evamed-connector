package fr.has.evamed.connector.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "ref_liste_ma_page")
public class RefListeMaPage {

    @EmbeddedId
    private RefListeMaPageId id;

    @MapsId("utId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ut_id", nullable = false)
    private Utilisateur utilisateur;

    @Column(name = "imp_libelle", length = 50)
    private String impLibelle;

    @Column(name = "imp_date_sup_log")
    private Instant impDateSupLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tda_code")
    private RefTypeDossierAssocie typeDossierAssocie;

    @Column(name = "ordre")
    private Integer ordre;

    public RefListeMaPageId getId() {
        return id;
    }

    public void setId(RefListeMaPageId id) {
        this.id = id;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public String getImpLibelle() {
        return impLibelle;
    }

    public void setImpLibelle(String impLibelle) {
        this.impLibelle = impLibelle;
    }

    public Instant getImpDateSupLog() {
        return impDateSupLog;
    }

    public void setImpDateSupLog(Instant impDateSupLog) {
        this.impDateSupLog = impDateSupLog;
    }

    public RefTypeDossierAssocie getTypeDossierAssocie() {
        return typeDossierAssocie;
    }

    public void setTypeDossierAssocie(RefTypeDossierAssocie typeDossierAssocie) {
        this.typeDossierAssocie = typeDossierAssocie;
    }

    public Integer getOrdre() {
        return ordre;
    }

    public void setOrdre(Integer ordre) {
        this.ordre = ordre;
    }
}
