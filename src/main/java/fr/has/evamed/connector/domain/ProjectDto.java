package fr.has.evamed.connector.domain;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class ProjectDto {
    private String id;
    private String number;
    private String type;
    private String title;
    private Boolean isSensitive;
    private String applicationDomain;
    private List<PhaseDto> phases;
    private List<ProjectManagerDto> projectManagers;
    private String managementAssistant;
    private LocalDateTime creationAt;
    private LocalDateTime endAt;
}
