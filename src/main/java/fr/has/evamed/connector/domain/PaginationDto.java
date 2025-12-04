package fr.has.evamed.connector.domain;

import lombok.Data;

@Data
public class PaginationDto {
    private Integer offset;
    private Integer limit;
    private Integer totalItems;
    private Integer totalPages;
}
