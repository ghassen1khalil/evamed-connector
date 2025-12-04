package fr.has.evamed.connector.domain;

import lombok.Data;

@Data
public class ProjectManagerDto {
    private String firstName;
    private String lastName;
    private Boolean isPrimary;
    private Boolean isSecondary;
}
