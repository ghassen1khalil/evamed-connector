package fr.has.evamed.connector.repository;

import fr.has.evamed.connector.domain.RefListeMaPage;
import fr.has.evamed.connector.domain.RefListeMaPageId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefListeMaPageRepository extends JpaRepository<RefListeMaPage, RefListeMaPageId> {
}
