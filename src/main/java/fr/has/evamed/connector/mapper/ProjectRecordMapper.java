package fr.has.evamed.connector.mapper;

import fr.has.evamed.connector.domain.ProjectDto;
import org.jooq.Record;
import org.jooq.RecordMapper;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.name;
import org.jooq.Field;

/**
 * Maps a jOOQ Record originating from evamed.dossier to ProjectDto.
 * Explicitly declares the fields to avoid fragile index-based access.
 */
public class ProjectRecordMapper implements RecordMapper<Record, ProjectDto> {

    public static final Field<Long> DOS_ID = field(name("evamed", "dossier", "dos_id"), Long.class);
    public static final Field<Integer> DOS_NUMERO = field(name("evamed", "dossier", "dos_numero"), Integer.class);
    public static final Field<String> DOS_TITRE = field(name("evamed", "dossier", "dos_titre"), String.class);

    @Override
    public ProjectDto map(Record record) {
        Long dosId = record.get(DOS_ID);
        Integer dosNumero = record.get(DOS_NUMERO);
        String dosTitre = record.get(DOS_TITRE);

        ProjectDto dto = new ProjectDto();
        if (dosId != null) {
            dto.setId(IdConverters.uuidFromLong(dosId));
        }
        if (dosNumero != null) {
            dto.setNumber(String.valueOf(dosNumero));
        }
        if (dosTitre != null) {
            dto.setTitle(dosTitre);
        }
        return dto;
    }
}
