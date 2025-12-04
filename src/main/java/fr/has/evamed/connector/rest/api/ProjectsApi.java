package fr.has.evamed.connector.rest.api;

import fr.has.evamed.connector.domain.PaginatedProjectsDto;
import fr.has.evamed.connector.domain.ProjectDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Validated
public interface ProjectsApi {

    @GetMapping(path = "/projects")
    ResponseEntity<PaginatedProjectsDto> listProjects(
            @RequestParam(name = "offset", required = false, defaultValue = "0") Integer offset,
            @RequestParam(name = "limit", required = false, defaultValue = "20") Integer limit);

    @GetMapping(path = "/projects/{projectId}")
    ResponseEntity<ProjectDto> getProjectById(@PathVariable("projectId") String projectId);
}
