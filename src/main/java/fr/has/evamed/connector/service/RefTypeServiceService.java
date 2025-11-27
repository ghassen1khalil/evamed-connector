package fr.has.evamed.connector.service;

import fr.has.evamed.connector.dto.RefTypeServiceDto;
import fr.has.evamed.connector.mapper.RefTypeServiceMapper;
import fr.has.evamed.connector.repository.RefTypeServiceRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class RefTypeServiceService {

    private final RefTypeServiceRepository repository;
    private final RefTypeServiceMapper mapper = new RefTypeServiceMapper();

    public RefTypeServiceService(RefTypeServiceRepository repository) {
        this.repository = repository;
    }

    public List<RefTypeServiceDto> getAllRefTypeServices() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public Optional<RefTypeServiceDto> getRefTypeServiceById(String code) {
        return repository.findById(code).map(mapper::toDto);
    }
}
