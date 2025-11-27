package fr.has.evamed.connector.service;

import fr.has.evamed.connector.dto.MaPageUtilisateurDto;
import fr.has.evamed.connector.mapper.MaPageUtilisateurMapper;
import fr.has.evamed.connector.repository.MaPageUtilisateurRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class MaPageUtilisateurService {

    private final MaPageUtilisateurRepository repository;
    private final MaPageUtilisateurMapper mapper = new MaPageUtilisateurMapper();

    public MaPageUtilisateurService(MaPageUtilisateurRepository repository) {
        this.repository = repository;
    }

    public List<MaPageUtilisateurDto> getAllMaPageUtilisateurs() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public Optional<MaPageUtilisateurDto> getMaPageUtilisateurById(String code) {
        return repository.findById(code).map(mapper::toDto);
    }
}
