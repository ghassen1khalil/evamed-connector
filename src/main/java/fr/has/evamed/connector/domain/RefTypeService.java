package fr.has.evamed.connector.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "ref_type_service")
public class RefTypeService {

    @Id
    @Column(name = "srv_code", length = 20, nullable = false)
    private String srvCode;

    @Column(name = "srv_libelle", length = 50)
    private String srvLibelle;

    @Column(name = "srv_date_sup_log")
    private Instant srvDateSupLog;

    public String getSrvCode() {
        return srvCode;
    }

    public void setSrvCode(String srvCode) {
        this.srvCode = srvCode;
    }

    public String getSrvLibelle() {
        return srvLibelle;
    }

    public void setSrvLibelle(String srvLibelle) {
        this.srvLibelle = srvLibelle;
    }

    public Instant getSrvDateSupLog() {
        return srvDateSupLog;
    }

    public void setSrvDateSupLog(Instant srvDateSupLog) {
        this.srvDateSupLog = srvDateSupLog;
    }
}
