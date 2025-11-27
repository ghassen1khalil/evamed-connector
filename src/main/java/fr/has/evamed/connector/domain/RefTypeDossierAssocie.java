package fr.has.evamed.connector.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "ref_type_dossier_associe")
public class RefTypeDossierAssocie {

    @Id
    @Column(name = "tda_code", length = 20, nullable = false)
    private String tdaCode;

    @Column(name = "tda_libelle", length = 50)
    private String tdaLibelle;

    @Column(name = "tda_date_sup_log")
    private Instant tdaDateSupLog;

    public String getTdaCode() {
        return tdaCode;
    }

    public void setTdaCode(String tdaCode) {
        this.tdaCode = tdaCode;
    }

    public String getTdaLibelle() {
        return tdaLibelle;
    }

    public void setTdaLibelle(String tdaLibelle) {
        this.tdaLibelle = tdaLibelle;
    }

    public Instant getTdaDateSupLog() {
        return tdaDateSupLog;
    }

    public void setTdaDateSupLog(Instant tdaDateSupLog) {
        this.tdaDateSupLog = tdaDateSupLog;
    }
}
