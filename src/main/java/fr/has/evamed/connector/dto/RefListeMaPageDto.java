package fr.has.evamed.connector.dto;

import java.time.Instant;

public class RefListeMaPageDto {
    private String impCode;
    private Long utId;
    private String impLibelle;
    private Instant impDateSupLog;
    private String tdaCode;
    private Integer ordre;

    public String getImpCode() {
        return impCode;
    }

    public void setImpCode(String impCode) {
        this.impCode = impCode;
    }

    public Long getUtId() {
        return utId;
    }

    public void setUtId(Long utId) {
        this.utId = utId;
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

    public String getTdaCode() {
        return tdaCode;
    }

    public void setTdaCode(String tdaCode) {
        this.tdaCode = tdaCode;
    }

    public Integer getOrdre() {
        return ordre;
    }

    public void setOrdre(Integer ordre) {
        this.ordre = ordre;
    }
}
