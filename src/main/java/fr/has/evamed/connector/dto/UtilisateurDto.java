package fr.has.evamed.connector.dto;

import java.time.Instant;

public class UtilisateurDto {
    private Long utId;
    private String prfCode;
    private String srvCode;
    private String utNom;
    private String utPrenom;
    private String utLogin;
    private String utTelephone;
    private String utFax;
    private String utEmail;
    private Boolean utArchive;
    private Instant utDateModif;
    private String utDenomNorm;

    public Long getUtId() {
        return utId;
    }

    public void setUtId(Long utId) {
        this.utId = utId;
    }

    public String getPrfCode() {
        return prfCode;
    }

    public void setPrfCode(String prfCode) {
        this.prfCode = prfCode;
    }

    public String getSrvCode() {
        return srvCode;
    }

    public void setSrvCode(String srvCode) {
        this.srvCode = srvCode;
    }

    public String getUtNom() {
        return utNom;
    }

    public void setUtNom(String utNom) {
        this.utNom = utNom;
    }

    public String getUtPrenom() {
        return utPrenom;
    }

    public void setUtPrenom(String utPrenom) {
        this.utPrenom = utPrenom;
    }

    public String getUtLogin() {
        return utLogin;
    }

    public void setUtLogin(String utLogin) {
        this.utLogin = utLogin;
    }

    public String getUtTelephone() {
        return utTelephone;
    }

    public void setUtTelephone(String utTelephone) {
        this.utTelephone = utTelephone;
    }

    public String getUtFax() {
        return utFax;
    }

    public void setUtFax(String utFax) {
        this.utFax = utFax;
    }

    public String getUtEmail() {
        return utEmail;
    }

    public void setUtEmail(String utEmail) {
        this.utEmail = utEmail;
    }

    public Boolean getUtArchive() {
        return utArchive;
    }

    public void setUtArchive(Boolean utArchive) {
        this.utArchive = utArchive;
    }

    public Instant getUtDateModif() {
        return utDateModif;
    }

    public void setUtDateModif(Instant utDateModif) {
        this.utDateModif = utDateModif;
    }

    public String getUtDenomNorm() {
        return utDenomNorm;
    }

    public void setUtDenomNorm(String utDenomNorm) {
        this.utDenomNorm = utDenomNorm;
    }
}
