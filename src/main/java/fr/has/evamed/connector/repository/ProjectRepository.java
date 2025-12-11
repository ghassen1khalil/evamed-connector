package fr.has.evamed.connector.repository;

import fr.has.evamed.connector.domain.ProjectDto;
import fr.has.evamed.connector.mapper.ProjectRecordMapper;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDate;

import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.DSL.selectCount;

@Repository
public class ProjectRepository {

    private final DSLContext context;

    public ProjectRepository(DSLContext context) {
        this.context = context;
    }

    public List<ProjectDto> getProjects(Integer offset, Integer limit) {
        // Shortcut fields
        var DOS_ID_LONG = field(name("evamed", "dossier", "dos_id"), Long.class);

        // Computed/planned date fields using DB functions
        var PREV_DEBUT_CADRAGE = field("pkg_dossier.f_get_date_pre_dcad({0})", LocalDate.class, DOS_ID_LONG).as("prev_debut_cadrage");
        var PREV_CADRAGE = field("pkg_dossier.f_get_date_pre_cad({0})", LocalDate.class, DOS_ID_LONG).as("prev_cadrage");
        var PREV_PGT = field("pkg_dossier.f_get_date_pre_pgt({0})", LocalDate.class, DOS_ID_LONG).as("prev_pgt");
        var PREV_DGT = field("pkg_dossier.f_get_date_pre_dgt({0})", LocalDate.class, DOS_ID_LONG).as("prev_dgt");
        var PREV_EXAMEN = field("pkg_dossier.f_get_date_pre_exa({0})", LocalDate.class, DOS_ID_LONG).as("prev_examen");
        var PREV_VALIDATION = field("pkg_dossier.f_get_date_pre_val({0})", LocalDate.class, DOS_ID_LONG).as("prev_validation");
        var PREV_MISE_EN_LIGNE = field("pkg_dossier.f_get_date_pre_dif({0})", LocalDate.class, DOS_ID_LONG).as("prev_mise_en_ligne");
        var PREV_CLOTURE = field("pkg_dossier.f_get_date_pre_clo({0})", LocalDate.class, DOS_ID_LONG).as("prev_cloture");

        // Subquery for sensible flag
        var amp = table(name("evamed", "autre_membre_projet")).as("amp");
        var SENSIBLE = selectCount()
                .from(amp)
                .where(field(name("amp", "utl_id"), Integer.class).eq(9461)
                        .and(field(name("amp", "amp_aut"), Integer.class).eq(1))
                        .and(field(name("amp", "dos_id"), Long.class).eq(DOS_ID_LONG)))
                .asField("sensible");

        var records = context
                .select(
                        ProjectRecordMapper.DOS_ID,
                        ProjectRecordMapper.DOS_NUMERO,
                        ProjectRecordMapper.TDOS_CODE,
                        ProjectRecordMapper.DOS_TITRE,
                        ProjectRecordMapper.PLA_COM,
                        ProjectRecordMapper.DOS_DATE_SAISINE,
                        ProjectRecordMapper.DOS_DATE_DEBUT_CADRAGE,
                        ProjectRecordMapper.DOS_DATE_CADRAGE,
                        ProjectRecordMapper.DOS_DATE_PGT,
                        ProjectRecordMapper.DOS_DATE_DGT,
                        ProjectRecordMapper.DOS_DATE_EXAMEN,
                        ProjectRecordMapper.DOS_DATE_VALIDATION,
                        ProjectRecordMapper.DOS_DATE_MISE_EN_LIGNE,
                        ProjectRecordMapper.DOS_DATE_CLOTURE,
                        PREV_DEBUT_CADRAGE,
                        PREV_CADRAGE,
                        PREV_PGT,
                        PREV_DGT,
                        PREV_EXAMEN,
                        PREV_VALIDATION,
                        PREV_MISE_EN_LIGNE,
                        PREV_CLOTURE,
                        SENSIBLE
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
