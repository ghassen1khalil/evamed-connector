package fr.has.evamed.connector.service;

import fr.has.evamed.connector.dto.RefProfilRefRoleDto;
import fr.has.evamed.connector.mapper.RefProfilRefRoleMapper;
import fr.has.evamed.connector.repository.RefProfilRefRoleRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class RefProfilRefRoleService {

    private final RefProfilRefRoleRepository repository;
    private final RefProfilRefRoleMapper mapper = new RefProfilRefRoleMapper();

    public RefProfilRefRoleService(RefProfilRefRoleRepository repository) {
        this.repository = repository;
    }

    public List<RefProfilRefRoleDto> getAllRefProfilRefRoles() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public Optional<RefProfilRefRoleDto> getRefProfilRefRoleById(String prfCode, String rolCode) {
        fr.has.evamed.connector.domain.RefProfilRefRoleId id = new fr.has.evamed.connector.domain.RefProfilRefRoleId();
        id.setPrfCode(prfCode);
        id.setRolCode(rolCode);
        return repository.findById(id).map(mapper::toDto);
    }
}
