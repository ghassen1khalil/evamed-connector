package fr.has.evamed.connector.service;

import fr.has.evamed.connector.dto.RefRoleDto;
import fr.has.evamed.connector.mapper.RefRoleMapper;
import fr.has.evamed.connector.repository.RefRoleRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class RefRoleService {

    private final RefRoleRepository repository;
    private final RefRoleMapper mapper = new RefRoleMapper();

    public RefRoleService(RefRoleRepository repository) {
        this.repository = repository;
    }

    public List<RefRoleDto> getAllRefRoles() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public Optional<RefRoleDto> getRefRoleById(String code) {
        return repository.findById(code).map(mapper::toDto);
    }
}
