package fr.has.evamed.connector.service;

import fr.has.evamed.connector.domain.ListePartageUtilisateurId;
import fr.has.evamed.connector.dto.ListePartageUtilisateurDto;
import fr.has.evamed.connector.mapper.ListePartageUtilisateurMapper;
import fr.has.evamed.connector.repository.ListePartageUtilisateurRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ListePartageUtilisateurService {

    private final ListePartageUtilisateurRepository repository;
    private final ListePartageUtilisateurMapper mapper = new ListePartageUtilisateurMapper();

    public ListePartageUtilisateurService(ListePartageUtilisateurRepository repository) {
        this.repository = repository;
    }

    public List<ListePartageUtilisateurDto> getAllListePartageUtilisateurs() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public Optional<ListePartageUtilisateurDto> getListePartageUtilisateurById(Long mseId, Long utId) {
        ListePartageUtilisateurId id = new ListePartageUtilisateurId();
        id.setMseId(mseId);
        id.setUtId(utId);
        return repository.findById(id).map(mapper::toDto);
    }
}
