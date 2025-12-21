package fr.has.evamed.connector.controller;

import fr.has.evamed.connector.domain.*;
import fr.has.evamed.connector.rest.api.ProjectsApi;
import fr.has.evamed.connector.service.ProjectService;
import fr.has.evamed.connector.utils.CommonUtils;
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

    @NonNull private final ProjectService projectService;

    @Override
    public ResponseEntity<PaginatedProjectResponseDto> getProjects(UserTypeDto userType,
                                                                   Integer offset,
                                                                   Integer limit,
                                                                   List<String> projectManagerId,
                                                                   List<String> managementAssistantId,
                                                                   List<String> projectType,
                                                                   Boolean isSensitive,
                                                                   Boolean isProjectFinished,
                                                                   ProjectPhaseFilterDto phase,
                                                                   PeriodFilterDto period) {
        log.info("Projects Api - Getting projects");
        return ResponseEntity.ok(projectService.getProjects(CommonUtils
                .buildFilters(userType, offset, limit, projectManagerId, managementAssistantId, projectType, isSensitive,
                        isProjectFinished, phase, period)));
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
    public ResponseEntity<PaginatedProjectsByUsersResponseDto> getProjectsByManagers(UserTypeDto userType,
                                                                                        Integer offset,
                                                                                        Integer limit,
                                                                                        List<String> projectManagerId,
                                                                                        List<String> managementAssistantId,
                                                                                        List<String> projectType,
                                                                                        Boolean isSensitive,
                                                                                        Boolean isProjectFinished,
                                                                                        ProjectPhaseFilterDto phase,
                                                                                        PeriodFilterDto period) {
        log.info("Projects Api - Getting projects for managers (paginated)");
        return ResponseEntity.ok(projectService.getProjectsByManagers(CommonUtils
                .buildFilters(userType, offset, limit, projectManagerId, managementAssistantId, projectType, isSensitive,
                        isProjectFinished, phase, period)));
    }
}
