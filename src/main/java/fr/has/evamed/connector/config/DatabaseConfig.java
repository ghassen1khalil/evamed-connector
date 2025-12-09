package fr.has.evamed.connector.config;

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.sql.Connection;
import java.sql.DriverManager;

@Slf4j
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String dbUrl;
    @Value("${spring.datasource.username}")
    private String dbUser;
    @Value("${spring.datasource.password}")
    private String dbPassword;


    @Bean
    public DSLContext getDSLContext() {
        DSLContext context;
        try {
            Connection connnection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            context = DSL.using(connnection, SQLDialect.POSTGRES);
        } catch (Exception e) {
            log.error("Error while connecting to database", e);
            throw new RuntimeException(e); //TODO change this exception with a custom one
        }
        return context;

    }
}
