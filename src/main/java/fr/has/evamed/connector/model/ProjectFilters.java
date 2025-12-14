package fr.has.evamed.connector.model;

import fr.has.evamed.connector.domain.PeriodFilterDto;
import fr.has.evamed.connector.domain.ProjectPhaseFilterDto;
import fr.has.evamed.connector.domain.UserTypeDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProjectFilters {
    Integer offset;
    Integer limit;
    UserTypeDto userType;
    List<String> projectManagerId;
    List<String> managementAssistantId;
    List<String> projectsTypes;
    Boolean isSensitive;
    Boolean isFinished;
    ProjectPhaseFilterDto phase;
    PeriodFilterDto period;
}
