package fr.has.evamed.connector.service;

import fr.has.evamed.connector.dto.RefTypeDossierAssocieDto;
import fr.has.evamed.connector.mapper.RefTypeDossierAssocieMapper;
import fr.has.evamed.connector.repository.RefTypeDossierAssocieRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class RefTypeDossierAssocieService {

    private final RefTypeDossierAssocieRepository repository;
    private final RefTypeDossierAssocieMapper mapper = new RefTypeDossierAssocieMapper();

    public RefTypeDossierAssocieService(RefTypeDossierAssocieRepository repository) {
        this.repository = repository;
    }

    public List<RefTypeDossierAssocieDto> getAllRefTypeDossierAssocies() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public Optional<RefTypeDossierAssocieDto> getRefTypeDossierAssocieById(String code) {
        return repository.findById(code).map(mapper::toDto);
    }
}
