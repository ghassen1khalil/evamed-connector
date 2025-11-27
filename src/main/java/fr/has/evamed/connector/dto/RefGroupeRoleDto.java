package fr.has.evamed.connector.dto;

import java.time.Instant;

public class RefGroupeRoleDto {
    private String grrCode;
    private String grrLibelle;
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
