package fr.has.evamed.connector.repository;

import fr.has.evamed.connector.domain.ProjectDto;
import fr.has.evamed.connector.mapper.ProjectRecordMapper;
import fr.has.evamed.domain.entities.Tables;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.selectCount;

@Repository
@Slf4j
public class ProjectRepository {

    private final DSLContext context;

    // Constants (good practices: no magic numbers/strings inline)
    private static final int SECONDS_PER_DAY = 86_400;
    private static final long SENSIBLE_USER_ID = 9_461L;
    private static final short SENSIBLE_AUT_FLAG = 1;
    private static final String DEFAULT_LABEL = "Aucun";
    private static final String COL_DELAI_MOYEN = "delai_moyen";

    public ProjectRepository(DSLContext context) {
        this.context = context;
    }

    public List<ProjectDto> getProjects(Integer offset, Integer limit) {
        // Shortcut fields using generated jOOQ table
        var DOS_ID_LONG = Tables.DOSSIER.DOS_ID;

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
        var AMP = Tables.AUTRE_MEMBRE_PROJET.as("amp");
        var SENSIBLE = selectCount()
                .from(AMP)
                .where(AMP.UTL_ID.eq(DSL.inline(SENSIBLE_USER_ID))
                        .and(AMP.AMP_AUT.eq(DSL.inline(SENSIBLE_AUT_FLAG)))
                        .and(AMP.DOS_ID.eq(DOS_ID_LONG)))
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
                .from(Tables.DOSSIER)
                .offset(offset)
                .limit(limit)
                .fetch();

        // Map records to DTOs using a reusable mapper
        return records.map(new ProjectRecordMapper());
    }

    public int countProjects() {
        return context.fetchCount(Tables.DOSSIER);
    }

    public String getProjectType(String dosId) {
        Long id;
        try {
            id = dosId != null ? Long.valueOf(dosId) : null;
        } catch (NumberFormatException e) {
            return null; // Invalid id format
        }
        if (id == null) return null;

        var RTDE = Tables.REF_TYPE_DOSSIER_EVAL.as("rtde");
        var D = Tables.DOSSIER.as("d");

        return context
                .select(RTDE.REGROUP)
                .from(D.join(RTDE).on(RTDE.TDE_CODE.eq(D.TDE_CODE)))
                .where(D.DOS_ID.eq(id))
                .fetchOne(RTDE.REGROUP);
    }

    /**
     * Délai moyen par typologie.
     * Traduction jOOQ/SQL de la requête fournie. Retourne une Map où la clé est rtde.regroup
     * et la valeur est le délai moyen (arrondi) en jours.
     */
    public Map<String, Integer> averageTimePerTypology() {
        log.info("Requesting database for calculating average time per typology ...");
        var D = Tables.DOSSIER.as("d");
        var RTDE = Tables.REF_TYPE_DOSSIER_EVAL.as("rtde");
        var SD = Tables.SUSPENSION_DELAI.as("sd");

        // extract(epoch from d.dos_date_cloture - d.dos_date_debut_cadrage)/86400
        var mainDays = DSL.field(
                "extract(epoch from {0} - {1}) / {2}",
                Double.class,
                D.DOS_DATE_CLOTURE, D.DOS_DATE_DEBUT_CADRAGE, DSL.inline(SECONDS_PER_DAY)
        );

        // coalesce((select sum(extract(epoch from sd.spd_date_de_fin - sd.spd_date_de_debut)/86400) from suspension_delai sd where sd.dos_id = d.dos_id), 0)
        // Build this as a proper jOOQ subselect so the table renders correctly as "suspension_delai sd"
        var suspDaysSubSelect = DSL
                .select(
                        DSL.sum(
                                DSL.field(
                                        "extract(epoch from {0} - {1}) / {2}",
                                        Double.class,
                                        SD.SPD_DATE_DE_FIN,
                                        SD.SPD_DATE_DE_DEBUT,
                                        DSL.inline(SECONDS_PER_DAY)
                                )
                        ).as("sum_days")
                )
                .from(SD)
                .where(SD.DOS_ID.eq(D.DOS_ID));

        var suspDays = DSL.coalesce(suspDaysSubSelect.asField(), DSL.inline(0d));

        var expr = DSL.field("({0} - {1})", Double.class, mainDays, suspDays);

        var delaiMoyen = DSL.round(DSL.avg(expr), 0).cast(Integer.class).as(COL_DELAI_MOYEN);

        var result = context
                .select(RTDE.REGROUP, delaiMoyen)
                .from(D.leftJoin(RTDE).on(D.TDE_CODE.eq(RTDE.TDE_CODE)))
                .groupBy(RTDE.REGROUP)
                .fetch();

        Map<String, Integer> map = new HashMap<>();
        result.forEach(rec -> {
            String key = rec.get(RTDE.REGROUP);
            Integer value = rec.get(COL_DELAI_MOYEN, Integer.class);
            if (value != null) {
                map.put(key != null ? key : DEFAULT_LABEL, value);
            }
        });
        return map;
    }


}
