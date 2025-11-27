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
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ma_selection")
public class MaSelection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mse_id", nullable = false)
    private Long mseId;

    @Column(name = "mse_libelle", length = 250, nullable = false)
    private String mseLibelle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ut_id", nullable = false)
    private Utilisateur utilisateur;

    @OneToMany(mappedBy = "selection")
    private Set<ListePartageUtilisateur> partages = new HashSet<>();

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

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Set<ListePartageUtilisateur> getPartages() {
        return partages;
    }

    public void setPartages(Set<ListePartageUtilisateur> partages) {
        this.partages = partages;
    }
}
