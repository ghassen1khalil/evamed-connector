package fr.has.evamed.connector.repository;

import fr.has.evamed.connector.domain.ListePartageUtilisateur;
import fr.has.evamed.connector.domain.ListePartageUtilisateurId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListePartageUtilisateurRepository extends JpaRepository<ListePartageUtilisateur, ListePartageUtilisateurId> {
}
