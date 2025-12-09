package fr.has.evamed.connector.controller;

import fr.has.evamed.connector.domain.PaginatedProjectResponseDto;
import fr.has.evamed.connector.domain.ProjectDto;
import fr.has.evamed.connector.rest.api.ProjectsApi;
import fr.has.evamed.connector.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ProjectsController implements ProjectsApi {

    private final ProjectService projectService;

    public ProjectsController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    public ResponseEntity<ProjectDto> getProjectById(String projectId) {
        return ProjectsApi.super.getProjectById(projectId);
    }

    @Override
    public ResponseEntity<Map<String, List<ProjectDto>>> getProjectsForAssistant() {
        return ProjectsApi.super.getProjectsForAssistant();
    }

    @Override
    public ResponseEntity<Map<String, List<ProjectDto>>> getProjectsForManager() {
        return ProjectsApi.super.getProjectsForManager();
    }

    @Override
    public ResponseEntity<Map<String, List<ProjectDto>>> getProjectsPerDomain() {
        return ProjectsApi.super.getProjectsPerDomain();
    }

    @Override
    public ResponseEntity<Map<String, List<ProjectDto>>> getProjectsPerPhase() {
        return ProjectsApi.super.getProjectsPerPhase();
    }

    @Override
    public ResponseEntity<Map<String, List<ProjectDto>>> getProjectsPerType() {
        return ProjectsApi.super.getProjectsPerType();
    }

    @Override
    public ResponseEntity<PaginatedProjectResponseDto> getProjects(Integer offset, Integer limit, String projectManagerId, String managementAssistantId, String sortBy, String sortDirection) {
        PaginatedProjectResponseDto result = projectService.getProjects(offset, limit);
        return ResponseEntity.ok(result);
    }
}
