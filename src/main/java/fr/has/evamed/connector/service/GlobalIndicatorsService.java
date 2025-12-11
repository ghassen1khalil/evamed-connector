package fr.has.evamed.connector.service;

import fr.has.evamed.connector.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class GlobalIndicatorsService {

    private final ProjectRepository projectRepository;

    public GlobalIndicatorsService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Map<String, Integer> getGlobalIndicators() {
        return this.projectRepository.averageTimePerTypology();
    }
}
