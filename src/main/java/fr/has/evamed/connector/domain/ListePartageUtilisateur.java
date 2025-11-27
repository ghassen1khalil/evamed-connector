package fr.has.evamed.connector.domain;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "liste_partage_utilisateur")
public class ListePartageUtilisateur {

    @EmbeddedId
    private ListePartageUtilisateurId id;

    @MapsId("mseId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mse_id", nullable = false)
    private MaSelection selection;

    @MapsId("utId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ut_id", nullable = false)
    private Utilisateur utilisateur;

    public ListePartageUtilisateurId getId() {
        return id;
    }

    public void setId(ListePartageUtilisateurId id) {
        this.id = id;
    }

    public MaSelection getSelection() {
        return selection;
    }

    public void setSelection(MaSelection selection) {
        this.selection = selection;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
}
