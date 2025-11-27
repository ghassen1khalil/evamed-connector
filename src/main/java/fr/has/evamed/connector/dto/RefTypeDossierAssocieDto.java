package fr.has.evamed.connector.dto;

import java.time.Instant;

public class RefTypeDossierAssocieDto {
    private String tdaCode;
    private String tdaLibelle;
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
