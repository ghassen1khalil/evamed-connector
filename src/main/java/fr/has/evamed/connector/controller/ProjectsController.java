package fr.has.evamed.connector.controller;

import fr.has.evamed.connector.domain.PaginatedProjectsDto;
import fr.has.evamed.connector.domain.ProjectDto;
import fr.has.evamed.connector.rest.api.ProjectsApi;
import fr.has.evamed.connector.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
public class ProjectsController implements ProjectsApi {

    private final ProjectService projectService;

    public ProjectsController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    public ResponseEntity<PaginatedProjectsDto> listProjects(Integer offset, Integer limit) {
        PaginatedProjectsDto projects = projectService.listProjects(offset, limit);
        return ResponseEntity.status(OK).body(projects);
    }

    @Override
    public ResponseEntity<ProjectDto> getProjectById(String projectId) {
        ProjectDto project = projectService.getProjectById(projectId);
        return ResponseEntity.status(OK).body(project);
    }
}
