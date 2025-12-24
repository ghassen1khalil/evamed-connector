package fr.has.evamed.connector.controller;

import fr.has.evamed.connector.domain.ManagementAssistantDto;
import fr.has.evamed.connector.domain.PaginatedProjectResponseDto;
import fr.has.evamed.connector.domain.PaginatedProjectsByUsersResponseDto;
import fr.has.evamed.connector.domain.ProjectFiltersDto;
import fr.has.evamed.connector.domain.ProjectManagerDto;
import fr.has.evamed.connector.domain.UserTypeDto;
import fr.has.evamed.connector.model.UserProfile;
import fr.has.evamed.connector.rest.api.ProjectsApi;
import fr.has.evamed.connector.service.ProjectService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
public class ProjectsController implements ProjectsApi {

    @NonNull
    private final ProjectService projectService;

    @Override
    public ResponseEntity<PaginatedProjectResponseDto> getProjects(ProjectFiltersDto projectFilters, Integer offset, Integer limit) {
        log.info("Projects Api - Getting projects");
        ProjectFiltersDto filters = projectFilters == null ? new ProjectFiltersDto() : projectFilters;
        return ResponseEntity.ok(projectService.getProjects(projectFilters, offset, limit));
    }

    @Override
    public ResponseEntity<List<ProjectManagerDto>> getProjectManagers(UserTypeDto userType) {
        log.info("Projects Api - Getting project managers with userType={}", userType);
        return ResponseEntity.ok(projectService.getProjectManagers(userType));
    }

    @Override
    public ResponseEntity<List<ManagementAssistantDto>> getManagementAssistants(UserTypeDto userType) {
        log.info("Projects Api - Getting management assistants with userType={}", userType);
        return ResponseEntity.ok(projectService.getManagementAssistants(userType));
    }

    @Override
    public ResponseEntity<List<String>> getTypologies(UserTypeDto userType) {
        log.info("Projects Api - Getting typologies with userType={}", userType);
        return ResponseEntity.ok(projectService.getTypologies(userType));
    }

    @Override
    public ResponseEntity<PaginatedProjectsByUsersResponseDto> getProjectsByManagers(ProjectFiltersDto projectFilters, Integer offset, Integer limit) {
        log.info("Projects Api - Getting projects for managers (paginated)");
        return ResponseEntity.ok(projectService.getProjectsByUsers(UserProfile.MANAGER, projectFilters, offset, limit));
    }

    @Override
    public ResponseEntity<PaginatedProjectsByUsersResponseDto> getProjectsByAssistants(ProjectFiltersDto projectFilters, Integer offset, Integer limit) {
        log.info("Projects Api - Getting projects for assistants (paginated)");
        return ResponseEntity.ok(projectService.getProjectsByUsers(UserProfile.ASSISTANT, projectFilters, offset, limit));
    }
}
