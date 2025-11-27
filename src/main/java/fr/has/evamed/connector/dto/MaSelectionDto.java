package fr.has.evamed.connector.dto;

public class MaSelectionDto {
    private Long mseId;
    private String mseLibelle;
    private Long utId;

    public Long getMseId() {
        return mseId;
    }

    public void setMseId(Long mseId) {
        this.mseId = mseId;
    }

    public String getMseLibelle() {
        return mseLibelle;
    }

    public void setMseLibelle(String mseLibelle) {
        this.mseLibelle = mseLibelle;
    }

    public Long getUtId() {
        return utId;
    }

    public void setUtId(Long utId) {
        this.utId = utId;
    }
}
