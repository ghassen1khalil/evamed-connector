package fr.has.evamed.connector.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "ref_groupe_role")
public class RefGroupeRole {

    @Id
    @Column(name = "grr_code", length = 20, nullable = false)
    private String grrCode;

    @Column(name = "grr_libelle", length = 50)
    private String grrLibelle;

    @Column(name = "grr_date_sup_log")
    private Instant grrDateSupLog;

    public String getGrrCode() {
        return grrCode;
    }

    public void setGrrCode(String grrCode) {
        this.grrCode = grrCode;
    }

    public String getGrrLibelle() {
        return grrLibelle;
    }

    public void setGrrLibelle(String grrLibelle) {
        this.grrLibelle = grrLibelle;
    }

    public Instant getGrrDateSupLog() {
        return grrDateSupLog;
    }

    public void setGrrDateSupLog(Instant grrDateSupLog) {
        this.grrDateSupLog = grrDateSupLog;
    }
}
