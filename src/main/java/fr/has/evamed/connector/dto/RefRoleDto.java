package fr.has.evamed.connector.dto;

import java.time.Instant;

public class RefRoleDto {
    private String rolCode;
    private String rolLibelle;
    private Integer rolOrdre;
    private Instant rolDateSupLog;
    private String grrCode;

    public String getRolCode() {
        return rolCode;
    }

    public void setRolCode(String rolCode) {
        this.rolCode = rolCode;
    }

    public String getRolLibelle() {
        return rolLibelle;
    }

    public void setRolLibelle(String rolLibelle) {
        this.rolLibelle = rolLibelle;
    }

    public Integer getRolOrdre() {
        return rolOrdre;
    }

    public void setRolOrdre(Integer rolOrdre) {
        this.rolOrdre = rolOrdre;
    }

    public Instant getRolDateSupLog() {
        return rolDateSupLog;
    }

    public void setRolDateSupLog(Instant rolDateSupLog) {
        this.rolDateSupLog = rolDateSupLog;
    }

    public String getGrrCode() {
        return grrCode;
    }

    public void setGrrCode(String grrCode) {
        this.grrCode = grrCode;
    }
}
