package fr.has.evamed.connector.service;

import fr.has.evamed.connector.dto.RefProfilDto;
import fr.has.evamed.connector.mapper.RefProfilMapper;
import fr.has.evamed.connector.repository.RefProfilRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class RefProfilService {

    private final RefProfilRepository repository;
    private final RefProfilMapper mapper = new RefProfilMapper();

    public RefProfilService(RefProfilRepository repository) {
        this.repository = repository;
    }

    public List<RefProfilDto> getAllRefProfils() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public Optional<RefProfilDto> getRefProfilById(String code) {
        return repository.findById(code).map(mapper::toDto);
    }
}
