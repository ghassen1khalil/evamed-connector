package fr.has.evamed.connector.controller;

import fr.has.evamed.connector.domain.*;
import fr.has.evamed.connector.rest.api.ProjectsApi;
import fr.has.evamed.connector.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


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
    public ResponseEntity<PaginatedProjectResponseDto> getProjects(UserTypeDto userType, Integer offset, Integer limit, List<String> projectManagerId, List<String> managementAssistantId, List<String> projectType, Boolean isSensitive, Boolean isProjectFinished, ProjectPhaseFilterDto phase, PeriodFilterDto period, String sortBy, String sortDirection) {
        return ResponseEntity.ok(projectService.getProjects(offset, limit));
    }
}
