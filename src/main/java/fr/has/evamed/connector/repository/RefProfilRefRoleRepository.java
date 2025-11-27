package fr.has.evamed.connector.repository;

import fr.has.evamed.connector.domain.RefProfilRefRole;
import fr.has.evamed.connector.domain.RefProfilRefRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefProfilRefRoleRepository extends JpaRepository<RefProfilRefRole, RefProfilRefRoleId> {
}
