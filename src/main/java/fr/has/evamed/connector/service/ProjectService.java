package fr.has.evamed.connector.service;

import fr.has.evamed.connector.domain.PaginatedProjectResponseDto;
//import fr.has.evamed.connector.domain.PaginatedProjectsDto;
import fr.has.evamed.connector.domain.ProjectDto;
import lombok.extern.slf4j.Slf4j;
//import org.jooq.Record;
//import org.jooq.Result;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;

//import org.jooq.DSLContext;
//import org.jooq.SQLDialect;
//import org.jooq.impl.DSL;

@Service
@Slf4j
public class ProjectService {

    public PaginatedProjectResponseDto getProjects(Integer offset, Integer limit) {
        try {
            //TODO this is a WIP
            String userName = "evamed";
            String password = "evamed";
            String url = "jdbc:postgresql://10.10.200.15:5432/evamed";
            Connection conn = DriverManager.getConnection(url, userName, password);
            //DSLContext context = DSL.using(conn, SQLDialect.POSTGRES);
            //Result<Record> dossiers = context.select().from("dossier").fetch();
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return null;
    }

    public ProjectDto getProjectById(String projectId) {
        return null;
    }
}
