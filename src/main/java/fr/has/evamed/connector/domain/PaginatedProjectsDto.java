package fr.has.evamed.connector.domain;

import java.util.List;
import lombok.Data;

@Data
public class PaginatedProjectsDto {
    private List<ProjectDto> data;
    private PaginationDto pagination;
}
