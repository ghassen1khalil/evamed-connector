package fr.has.evamed.connector.service;

import fr.has.evamed.connector.dto.UtilisateurDto;
import fr.has.evamed.connector.mapper.UtilisateurMapper;
import fr.has.evamed.connector.repository.UtilisateurRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurMapper utilisateurMapper = new UtilisateurMapper();

    public UtilisateurService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public List<UtilisateurDto> getAllUtilisateurs() {
        return utilisateurRepository.findAll().stream()
            .map(utilisateurMapper::toDto)
            .collect(Collectors.toList());
    }

    public Optional<UtilisateurDto> getUtilisateurById(Long id) {
        return utilisateurRepository.findById(id).map(utilisateurMapper::toDto);
    }
}
