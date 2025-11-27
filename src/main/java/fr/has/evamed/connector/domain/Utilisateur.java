package fr.has.evamed.connector.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "utilisateur")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ut_id", nullable = false)
    private Long utId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prf_code")
    private RefProfil profil;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "srv_code")
    private RefTypeService typeService;

    @Column(name = "ut_nom", length = 50)
    private String utNom;

    @Column(name = "ut_prenom", length = 50)
    private String utPrenom;

    @Column(name = "ut_login", length = 50)
    private String utLogin;

    @Column(name = "ut_telephone", length = 20)
    private String utTelephone;

    @Column(name = "ut_fax", length = 20)
    private String utFax;

    @Column(name = "ut_email", length = 50)
    private String utEmail;

    @Column(name = "ut_archive")
    private Boolean utArchive;

    @Column(name = "ut_date_modif")
    private Instant utDateModif;

    @Column(name = "ut_denom_norm", length = 50, nullable = false)
    private String utDenomNorm;

    @OneToMany(mappedBy = "utilisateur")
    private Set<MaSelection> selections = new HashSet<>();

    @OneToMany(mappedBy = "utilisateur")
    private Set<ListePartageUtilisateur> partages = new HashSet<>();

    @OneToMany(mappedBy = "utilisateur")
    private Set<MaPageUtilisateur> pages = new HashSet<>();

    @OneToMany(mappedBy = "utilisateur")
    private Set<RefListeMaPage> listesMaPage = new HashSet<>();

    public Long getUtId() {
        return utId;
    }

    public void setUtId(Long utId) {
        this.utId = utId;
    }

    public RefProfil getProfil() {
        return profil;
    }

    public void setProfil(RefProfil profil) {
        this.profil = profil;
    }

    public RefTypeService getTypeService() {
        return typeService;
    }

    public void setTypeService(RefTypeService typeService) {
        this.typeService = typeService;
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

    public Set<MaSelection> getSelections() {
        return selections;
    }

    public void setSelections(Set<MaSelection> selections) {
        this.selections = selections;
    }

    public Set<ListePartageUtilisateur> getPartages() {
        return partages;
    }

    public void setPartages(Set<ListePartageUtilisateur> partages) {
        this.partages = partages;
    }

    public Set<MaPageUtilisateur> getPages() {
        return pages;
    }

    public void setPages(Set<MaPageUtilisateur> pages) {
        this.pages = pages;
    }

    public Set<RefListeMaPage> getListesMaPage() {
        return listesMaPage;
    }

    public void setListesMaPage(Set<RefListeMaPage> listesMaPage) {
        this.listesMaPage = listesMaPage;
    }
}
