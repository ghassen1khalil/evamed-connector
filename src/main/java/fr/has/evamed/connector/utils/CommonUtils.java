package fr.has.evamed.connector.utils;

import fr.has.evamed.connector.domain.PeriodFilterDto;
import fr.has.evamed.connector.domain.ProjectPhaseFilterDto;
import fr.has.evamed.connector.domain.UserTypeDto;
import fr.has.evamed.connector.model.ProjectFilters;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@UtilityClass
@Slf4j
public class CommonUtils {

    public static ProjectFilters buildFilters(UserTypeDto userType, Integer offset, Integer limit, List<String> projectManagerId, List<String> managementAssistantId, List<String> projectType, Boolean isSensitive, Boolean isProjectFinished, ProjectPhaseFilterDto phase, PeriodFilterDto period) {
        logActiveFilters(userType, offset, limit, projectManagerId, managementAssistantId, projectType, isSensitive, isProjectFinished, phase, period);
        return ProjectFilters.builder()
                .userType(userType)
                .offset(offset)
                .limit(limit)
                .projectManagerId(projectManagerId)
                .managementAssistantId(managementAssistantId)
                .projectsTypes(projectType)
                .isSensitive(isSensitive)
                .isFinished(isProjectFinished)
                .phase(phase)
                .period(period)
                .build();
    }


    private static void logActiveFilters(UserTypeDto userType, Integer offset, Integer limit, List<String> projectManagerId, List<String> managementAssistantId, List<String> projectType, Boolean isSensitive, Boolean isProjectFinished, ProjectPhaseFilterDto phase, PeriodFilterDto period) {
        StringBuilder filters = new StringBuilder("Active filters: ");
        boolean hasFilters = false;

        if (userType != null) {
            filters.append("userType=").append(userType).append(", ");
            hasFilters = true;
        }
        if (offset != null) {
            filters.append("offset=").append(offset).append(", ");
            hasFilters = true;
        }
        if (limit != null) {
            filters.append("limit=").append(limit).append(", ");
            hasFilters = true;
        }
        if (projectManagerId != null && !projectManagerId.isEmpty()) {
            filters.append("projectManagerId=").append(projectManagerId).append(", ");
            hasFilters = true;
        }
        if (managementAssistantId != null && !managementAssistantId.isEmpty()) {
            filters.append("managementAssistantId=").append(managementAssistantId).append(", ");
            hasFilters = true;
        }
        if (projectType != null && !projectType.isEmpty()) {
            filters.append("projectType=").append(projectType).append(", ");
            hasFilters = true;
        }
        if (isSensitive != null) {
            filters.append("isSensitive=").append(isSensitive).append(", ");
            hasFilters = true;
        }
        if (isProjectFinished != null) {
            filters.append("isProjectFinished=").append(isProjectFinished).append(", ");
            hasFilters = true;
        }
        if (phase != null) {
            filters.append("phase=").append(phase).append(", ");
            hasFilters = true;
        }
        if (period != null) {
            filters.append("period=").append(period).append(", ");
            hasFilters = true;
        }

        if (hasFilters) {
            String logMessage = filters.toString();
            logMessage = logMessage.substring(0, logMessage.length() - 2);
            log.info(logMessage);
        }
    }
}
