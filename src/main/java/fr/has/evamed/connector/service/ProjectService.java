package fr.has.evamed.connector.service;

import fr.has.evamed.connector.domain.PaginatedProjectResponseDto;
import fr.has.evamed.connector.domain.ProjectDto;
import fr.has.evamed.connector.mapper.ProjectRecordMapper;
import fr.has.evamed.connector.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.table;

@Service
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public PaginatedProjectResponseDto getProjects(Integer offset, Integer limit) {
        try {
            int off = offset != null ? offset : 0;
            int lim = limit != null ? limit : 20;
            List<ProjectDto> projects = projectRepository.getProjects(off, lim);
            int total = projectRepository.countProjects();
            return new PaginatedProjectResponseDto(projects, lim, off, total);
        }catch (Exception e){ //TODO Better exception handling
            log.error(e.getMessage());
        }
        return null;
    }

    public ProjectDto getProjectById(String projectId) {
        return null;
    }
}
