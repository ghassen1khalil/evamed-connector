package fr.has.evamed.connector.service;

import fr.has.evamed.connector.dto.MaSelectionDto;
import fr.has.evamed.connector.mapper.MaSelectionMapper;
import fr.has.evamed.connector.repository.MaSelectionRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class MaSelectionService {

    private final MaSelectionRepository repository;
    private final MaSelectionMapper mapper = new MaSelectionMapper();

    public MaSelectionService(MaSelectionRepository repository) {
        this.repository = repository;
    }

    public List<MaSelectionDto> getAllMaSelections() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public Optional<MaSelectionDto> getMaSelectionById(Long id) {
        return repository.findById(id).map(mapper::toDto);
    }
}
