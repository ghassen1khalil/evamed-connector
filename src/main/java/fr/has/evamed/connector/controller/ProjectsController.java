package fr.has.evamed.connector.controller;

import fr.has.evamed.connector.domain.*;
import fr.has.evamed.connector.model.ProjectFilters;
import fr.has.evamed.connector.rest.api.ProjectsApi;
import fr.has.evamed.connector.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@Slf4j
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
    public ResponseEntity<PaginatedProjectResponseDto> getProjects(UserTypeDto userType, Integer offset, Integer limit, List<String> projectManagerId, List<String> managementAssistantId, List<String> projectType, Boolean isSensitive, Boolean isProjectFinished, ProjectPhaseFilterDto phase, PeriodFilterDto period) {
        log.info("Projects Api - Getting projects with filters: userType={}, projectManagerId={}, managementAssistantId={}, projectType={}, isSensitive={}, isProjectFinished={}, phase={}, period={}", userType, projectManagerId, managementAssistantId, projectType, isSensitive, isProjectFinished, phase, period);
        ProjectFilters filters = ProjectFilters.builder()
                .userType(userType)
                .offset(offset)
                .limit(limit)
                .projectManagerId(projectManagerId)
                .managementAssistantId(managementAssistantId)
                .projectsTypes(projectType)
                .isSensitive(isSensitive)
                .isFinished(isProjectFinished)
                .phase(phase)
                .period(period)
                .build();
        return ResponseEntity.ok(projectService.getProjects(offset, limit, filters));
    }

    @Override
    public ResponseEntity<List<ProjectManagerDto>> getProjectManagers(UserTypeDto userType) {
        log.info("Projects Api - Getting project managers with userType={}", userType);
        return ResponseEntity.ok(projectService.getProjectManagers(userType));
    }

    @Override
    public ResponseEntity<List<ManagementAssistantDto>> getManagementAssistants() {
        log.info("Projects Api - Getting management assistants");
        return ResponseEntity.ok(projectService.getManagementAssistants());
    }

    @Override
    public ResponseEntity<List<String>> getTypologies(UserTypeDto userType) {
        log.info("Projects Api - Getting typologies with userType={}", userType);
        return ResponseEntity.ok(projectService.getTypologies(userType));
    }
}
