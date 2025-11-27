package fr.has.evamed.connector.service;

import fr.has.evamed.connector.dto.RefGroupeRoleDto;
import fr.has.evamed.connector.mapper.RefGroupeRoleMapper;
import fr.has.evamed.connector.repository.RefGroupeRoleRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class RefGroupeRoleService {

    private final RefGroupeRoleRepository repository;
    private final RefGroupeRoleMapper mapper = new RefGroupeRoleMapper();

    public RefGroupeRoleService(RefGroupeRoleRepository repository) {
        this.repository = repository;
    }

    public List<RefGroupeRoleDto> getAllRefGroupeRoles() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public Optional<RefGroupeRoleDto> getRefGroupeRoleById(String code) {
        return repository.findById(code).map(mapper::toDto);
    }
}
