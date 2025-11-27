package fr.has.evamed.connector.repository;

import fr.has.evamed.connector.domain.RefProfil;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefProfilRepository extends JpaRepository<RefProfil, String> {
}
