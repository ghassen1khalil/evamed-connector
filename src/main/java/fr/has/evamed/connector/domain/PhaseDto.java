package fr.has.evamed.connector.domain;

import java.time.LocalDate;
import lombok.Data;

@Data
public class PhaseDto {
    private LocalDate beginDate;
    private LocalDate endDate;
    private String label;
}
