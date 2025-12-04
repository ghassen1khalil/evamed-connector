package fr.has.evamed.connector.controller;

import fr.has.evamed.connector.domain.PaginatedProjectsDto;
import fr.has.evamed.connector.domain.ProjectDto;
import fr.has.evamed.connector.rest.api.ProjectsApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjectsController implements ProjectsApi {

    @Override
    public ResponseEntity<PaginatedProjectsDto> listProjects(Integer offset, Integer limit) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<ProjectDto> getProjectById(String projectId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
