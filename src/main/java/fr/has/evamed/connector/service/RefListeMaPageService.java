package fr.has.evamed.connector.service;

import fr.has.evamed.connector.domain.RefListeMaPageId;
import fr.has.evamed.connector.dto.RefListeMaPageDto;
import fr.has.evamed.connector.mapper.RefListeMaPageMapper;
import fr.has.evamed.connector.repository.RefListeMaPageRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class RefListeMaPageService {

    private final RefListeMaPageRepository repository;
    private final RefListeMaPageMapper mapper = new RefListeMaPageMapper();

    public RefListeMaPageService(RefListeMaPageRepository repository) {
        this.repository = repository;
    }

    public List<RefListeMaPageDto> getAllRefListeMaPages() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public Optional<RefListeMaPageDto> getRefListeMaPageById(String impCode, Long utId) {
        RefListeMaPageId id = new RefListeMaPageId();
        id.setImpCode(impCode);
        id.setUtId(utId);
        return repository.findById(id).map(mapper::toDto);
    }
}
