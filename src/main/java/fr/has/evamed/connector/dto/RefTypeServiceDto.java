package fr.has.evamed.connector.dto;

import java.time.Instant;

public class RefTypeServiceDto {
    private String srvCode;
    private String srvLibelle;
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
