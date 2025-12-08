package fr.has.evamed.connector.controller;

import fr.has.evamed.connector.domain.PaginatedProjectResponseDto;
import fr.has.evamed.connector.domain.ProjectDto;
import fr.has.evamed.connector.rest.api.ProjectsApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
public class ProjectsController implements ProjectsApi {

    @Override
    public ResponseEntity<ProjectDto> getProjectById(String projectId) {
        return ProjectsApi.super.getProjectById(projectId);
    }

    @Override
    public ResponseEntity<PaginatedProjectResponseDto> listProjects(Integer offset, Integer limit, String projectManagerId, String managementAssistantId, String number, String title, String type, String applicationDomain, Boolean isSensitive, OffsetDateTime creationAtFrom, OffsetDateTime creationAtTo, OffsetDateTime endAtFrom, OffsetDateTime endAtTo, String sortBy, String sortDirection) {
        return ProjectsApi.super.listProjects(offset, limit, projectManagerId, managementAssistantId, number, title, type, applicationDomain, isSensitive, creationAtFrom, creationAtTo, endAtFrom, endAtTo, sortBy, sortDirection);
    }
}
