package fr.has.evamed.connector.utils;

import fr.has.evamed.connector.domain.ProjectFiltersDto;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class LogUtils {

    public static void logActiveFilters(ProjectFiltersDto filtersDto) {
        StringBuilder filters = new StringBuilder("Active filters: ");
        boolean hasFilters = false;

        if (filtersDto.getUserType() != null) {
            filters.append("userType=").append(filtersDto.getUserType()).append(", ");
            hasFilters = true;
        }
        if (filtersDto.getProjectManagersIds() != null && !filtersDto.getProjectManagersIds().isEmpty()) {
            filters.append("projectManagersIds=").append(filtersDto.getProjectManagersIds()).append(", ");
            hasFilters = true;
        }
        if (filtersDto.getManagementAssistantsIds() != null && !filtersDto.getManagementAssistantsIds().isEmpty()) {
            filters.append("managementAssistantsIds=").append(filtersDto.getManagementAssistantsIds()).append(", ");
            hasFilters = true;
        }
        if (filtersDto.getProjectsTypes() != null && !filtersDto.getProjectsTypes().isEmpty()) {
            filters.append("projectsTypes=").append(filtersDto.getProjectsTypes()).append(", ");
            hasFilters = true;
        }
        if (filtersDto.getIsSensitive() != null) {
            filters.append("isSensitive=").append(filtersDto.getIsSensitive()).append(", ");
            hasFilters = true;
        }
        if (filtersDto.getIsProjectFinished() != null) {
            filters.append("isProjectFinished=").append(filtersDto.getIsProjectFinished()).append(", ");
            hasFilters = true;
        }
        if (filtersDto.getPhase() != null) {
            filters.append("phase=").append(filtersDto.getPhase()).append(", ");
            hasFilters = true;
        }
        if (filtersDto.getPeriod() != null) {
            filters.append("period=").append(filtersDto.getPeriod()).append(", ");
            hasFilters = true;
        }

        if (hasFilters) {
            String logMessage = filters.toString();
            logMessage = logMessage.substring(0, logMessage.length() - 2);
            log.info(logMessage);
        }
    }
}
