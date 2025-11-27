package fr.has.evamed.connector.dto;

import java.time.Instant;

public class MaPageUtilisateurDto {
    private String mpaCode;
    private Long utId;
    private String mpaLibelle;
    private Instant mpaDateSupLog;
    private Integer ordre;

    public String getMpaCode() {
        return mpaCode;
    }

    public void setMpaCode(String mpaCode) {
        this.mpaCode = mpaCode;
    }

    public Long getUtId() {
        return utId;
    }

    public void setUtId(Long utId) {
        this.utId = utId;
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
