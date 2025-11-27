package fr.has.evamed.connector.dto;

import java.time.Instant;

public class RefProfilDto {
    private String prfCode;
    private String prfLibelle;
    private Instant prfDateSupLog;

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
}
