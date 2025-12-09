package fr.has.evamed.connector.repository;

import fr.has.evamed.connector.domain.ProjectDto;
import fr.has.evamed.connector.mapper.ProjectRecordMapper;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
public class ProjectRepository {

    private final DSLContext context;

    public ProjectRepository(DSLContext context) {
        this.context = context;
    }

    public List<ProjectDto> getProjects(Integer offset, Integer limit) {
        var records = context
                .select(
                        ProjectRecordMapper.DOS_ID,
                        ProjectRecordMapper.DOS_NUMERO,
                        ProjectRecordMapper.DOS_TITRE
                )
                .from(table(name("evamed", "dossier")))
                .offset(offset)
                .limit(limit)
                .fetch();

        // Map records to DTOs using a reusable mapper
        return records.map(new ProjectRecordMapper());
    }

    public int countProjects() {
        return context.fetchCount(table(name("evamed", "dossier")));
    }

    public String getProjectType(String dosId) {
        Long id;
        try {
            id = dosId != null ? Long.valueOf(dosId) : null;
        } catch (NumberFormatException e) {
            return null; // Invalid id format
        }
        if (id == null) return null;

        var rtde = table(name("evamed", "ref_type_dossier_eval")).as("rtde");
        var d = table(name("evamed", "dossier")).as("d");

        return context
                .select(field(name("rtde", "regroup"), String.class))
                .from(rtde
                        .join(d)
                        .on(field(name("rtde", "tde_code")).eq(field(name("d", "tde_code")))))
                .where(field(name("d", "dos_id"), Long.class).eq(id))
                .fetchOne(0, String.class);
    }


}
