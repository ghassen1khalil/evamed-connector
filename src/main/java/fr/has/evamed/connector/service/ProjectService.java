package fr.has.evamed.connector.service;

import fr.has.evamed.connector.domain.PaginatedProjectResponseDto;
import fr.has.evamed.connector.domain.ProjectDto;
import fr.has.evamed.connector.domain.ProjectManagerDto;
import fr.has.evamed.connector.domain.ManagementAssistantDto;
import fr.has.evamed.connector.domain.UserTypeDto;
import fr.has.evamed.connector.model.ProjectFilters;
import fr.has.evamed.connector.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public PaginatedProjectResponseDto getProjects(Integer offset, Integer limit, ProjectFilters filters) {
        try {
            int off = offset != null ? offset : 0;
            int lim = limit != null ? limit : 20;
            log.info("Fetching projects with offset={} and limit={}", off, lim);
            List<ProjectDto> projects = projectRepository.getProjects(off, lim, filters);
            int total = projectRepository.countProjects(filters);
            return new PaginatedProjectResponseDto(projects, lim, off, total);
        } catch (Exception e) { //TODO Better exception handling
            log.error(e.getMessage());
        }
        return null;
    }


    public List<ProjectManagerDto> getProjectManagers(UserTypeDto userType) {
        return projectRepository.getProjectManagers(userType);
    }

    public List<ManagementAssistantDto> getManagementAssistants(UserTypeDto userType) {
        return projectRepository.getManagementAssistants(userType);
    }

    public List<String> getTypologies(UserTypeDto userType) {
        return projectRepository.getTypologies(userType);
    }
}
