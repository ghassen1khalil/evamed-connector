package fr.has.evamed.connector.repository;

import fr.has.evamed.connector.domain.ProjectDto;
import fr.has.evamed.connector.domain.ProjectPhaseFilterDto;
import fr.has.evamed.connector.domain.UserTypeDto;
import fr.has.evamed.connector.mapper.ProjectRecordMapper;
import fr.has.evamed.connector.model.ProjectFilters;
import fr.has.evamed.domain.entities.Tables;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private static final short FLAG_TRUE = 1;
    private static final String DEFAULT_LABEL = "Aucun";
    private static final String COL_DELAI_MOYEN = "delai_moyen";
    private static final String SBP_SERVICE_CODE = "DAQSS_SBPP";
    private static final String SR_SERVICE_CODE = "DIQASM_SR";

    public ProjectRepository(DSLContext context) {
        this.context = context;
    }

    /**
     * Retrieves a list of projects with pagination support.
     *
     * @param offset the starting index for the result set, used for pagination
     * @param limit the maximum number of projects to retrieve
     * @return a list of ProjectDto objects representing the projects
     */
    public List<ProjectDto> getProjects(Integer offset, Integer limit, ProjectFilters filters) {
        log.info("Requesting database for projects and applying filters ...");
        log.debug("Incoming filters for getProjects: {}", filters);
        try {
            var DOS_ID_LONG = Tables.DOSSIER.DOS_ID;
            var TDOS_CODE = Tables.DOSSIER.TDOS_CODE;

            // Computed/planned date fields using DB functions
            var PREV_DEBUT_CADRAGE = field("pkg_dossier.f_get_date_pre_dcad({0})", LocalDate.class, DOS_ID_LONG).as("prev_debut_cadrage");
            var PREV_CADRAGE = field("pkg_dossier.f_get_date_pre_cad({0})", LocalDate.class, DOS_ID_LONG).as("prev_cadrage");
            var PREV_PGT = field("pkg_dossier.f_get_date_pre_pgt({0})", LocalDate.class, DOS_ID_LONG).as("prev_pgt");
            var PREV_DGT = field("pkg_dossier.f_get_date_pre_dgt({0})", LocalDate.class, DOS_ID_LONG).as("prev_dgt");
            var PREV_EXAMEN = field("pkg_dossier.f_get_date_pre_exa({0})", LocalDate.class, DOS_ID_LONG).as("prev_examen");
            var PREV_VALIDATION = field("pkg_dossier.f_get_date_pre_val({0})", LocalDate.class, DOS_ID_LONG).as("prev_validation");
            var PREV_MISE_EN_LIGNE = field("pkg_dossier.f_get_date_pre_dif({0})", LocalDate.class, DOS_ID_LONG).as("prev_mise_en_ligne");
            var PREV_CLOTURE = field("pkg_dossier.f_get_date_pre_clo({0})", LocalDate.class, DOS_ID_LONG).as("prev_cloture");

            // Subquery for sensible flag (for selection only)
            var AMP = Tables.AUTRE_MEMBRE_PROJET.as("amp");
            var SENSIBLE = selectCount()
                    .from(AMP)
                    .where(AMP.UTL_ID.eq(DSL.inline(SENSIBLE_USER_ID))
                            .and(AMP.AMP_AUT.eq(DSL.inline(SENSIBLE_AUT_FLAG)))
                            .and(AMP.DOS_ID.eq(DOS_ID_LONG)))
                    .asField("sensible");

            var query = context
                    .selectDistinct(
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
                    .join(Tables.CHEF_PROJET).on(Tables.DOSSIER.DOS_ID.eq(Tables.CHEF_PROJET.DOS_ID))
                    .join(Tables.UTILISATEUR).on(Tables.CHEF_PROJET.UTL_ID.eq(Tables.UTILISATEUR.UTL_ID));

            // Build dynamic WHERE conditions
            Condition where = DSL.noCondition();

            // 1. User type (mandatory)
            where = where.and(buildUserTypeCondition(filters.getUserType(), TDOS_CODE, Tables.CHEF_PROJET.CPJ_EVALUE, Tables.UTILISATEUR.SRV_CODE));

            // 2. Period (optional)
            if (filters.getPeriod() != null && filters.getPeriod().getBeginDate() != null && filters.getPeriod().getEndDate() != null) {
                var begin = filters.getPeriod().getBeginDate();
                var end = filters.getPeriod().getEndDate();
                where = where.and(
                        buildPeriodCondition(
                                begin,
                                end,
                                Tables.DOSSIER.DOS_DATE_SAISINE,
                                Tables.DOSSIER.DOS_DATE_DEBUT_CADRAGE,
                                Tables.DOSSIER.DOS_DATE_CADRAGE,
                                Tables.DOSSIER.DOS_DATE_PGT,
                                Tables.DOSSIER.DOS_DATE_DGT,
                                Tables.DOSSIER.DOS_DATE_EXAMEN,
                                Tables.DOSSIER.DOS_DATE_VALIDATION,
                                Tables.DOSSIER.DOS_DATE_MISE_EN_LIGNE,
                                Tables.DOSSIER.DOS_DATE_CLOTURE
                        )
                );
            }

            // 3. Project manager IDs (optional)
            var pmIds = parseIds(filters.getProjectManagerId());
            if (!pmIds.isEmpty()) {
                where = where.and(Tables.CHEF_PROJET.UTL_ID.in(pmIds));
            }

            // 4. Management assistant (optional)
            var maIds = parseIds(filters.getManagementAssistantId());
            if (!maIds.isEmpty()) {
                var AMP_AG = Tables.AUTRE_MEMBRE_PROJET.as("amp_ag");
                query = query.join(AMP_AG).on(Tables.DOSSIER.DOS_ID.eq(AMP_AG.DOS_ID));
                where = where.and(AMP_AG.AMP_AG.eq(DSL.inline(FLAG_TRUE)))
                             .and(AMP_AG.UTL_ID.in(maIds));
            }

            // 5. Sensitivity (optional)
            if (Boolean.TRUE.equals(filters.getIsSensitive())) {
                var AMP_SENS = Tables.AUTRE_MEMBRE_PROJET.as("amp_sensible");
                query = query.join(AMP_SENS).on(Tables.DOSSIER.DOS_ID.eq(AMP_SENS.DOS_ID));
                where = where.and(AMP_SENS.UTL_ID.eq(DSL.inline(SENSIBLE_USER_ID)))
                             .and(AMP_SENS.AMP_AUT.eq(DSL.inline(SENSIBLE_AUT_FLAG)));
            }

            // 6. Typologies (optional)
            if (filters.getProjectsTypes() != null && !filters.getProjectsTypes().isEmpty()) {
                var RTDE = Tables.REF_TYPE_DOSSIER_EVAL.as("rtde");
                query = query.join(RTDE).on(RTDE.TDE_CODE.eq(Tables.DOSSIER.TDE_CODE));
                where = where.and(RTDE.REGROUP.in(filters.getProjectsTypes()));
            }

            // 7. Finished projects (optional)
            if (Boolean.TRUE.equals(filters.getIsFinished())) {
                where = where.and(Tables.DOSSIER.DOS_DATE_CLOTURE.isNotNull());
            }

            // 8. Phase (optional)
            if (filters.getPhase() != null) {
                String phaseCode = mapPhaseCode(filters.getPhase());
                if (phaseCode != null) {
                    where = where.and(Tables.DOSSIER.AVC_CODE.eq(phaseCode));
                }
            }

            log.debug("Built WHERE conditions for getProjects");

            var records = query
                    .where(where)
                    .offset(offset)
                    .limit(limit)
                    .fetch();

            return records.map(new ProjectRecordMapper());
        } catch (Exception e) {
            log.error("Failed to fetch projects with filters {}", filters, e);
            throw new IllegalStateException("Erreur lors de la récupération des projets", e);
        }
    }

    public int countProjects(ProjectFilters filters) {
        log.info("Requesting database for counting projects using filters ...");
        log.debug("Incoming filters for countProjects: {}", filters);
        try {
            var D = Tables.DOSSIER.as("d");
            var CP = Tables.CHEF_PROJET.as("cp");
            var U = Tables.UTILISATEUR.as("u");

            var query = context
                    .select(DSL.countDistinct(D.DOS_ID))
                    .from(D)
                    .join(CP).on(D.DOS_ID.eq(CP.DOS_ID))
                    .join(U).on(CP.UTL_ID.eq(U.UTL_ID));

            Condition where = DSL.noCondition();

            // 1. User type (mandatory)
            where = where.and(buildUserTypeCondition(filters.getUserType(), D.TDOS_CODE, CP.CPJ_EVALUE, U.SRV_CODE));

            // 2. Period (optional)
            if (filters.getPeriod() != null && filters.getPeriod().getBeginDate() != null && filters.getPeriod().getEndDate() != null) {
                where = where.and(
                        buildPeriodCondition(
                                filters.getPeriod().getBeginDate(),
                                filters.getPeriod().getEndDate(),
                                D.DOS_DATE_SAISINE,
                                D.DOS_DATE_DEBUT_CADRAGE,
                                D.DOS_DATE_CADRAGE,
                                D.DOS_DATE_PGT,
                                D.DOS_DATE_DGT,
                                D.DOS_DATE_EXAMEN,
                                D.DOS_DATE_VALIDATION,
                                D.DOS_DATE_MISE_EN_LIGNE,
                                D.DOS_DATE_CLOTURE
                        )
                );
            }

            // 3. Project manager IDs (optional)
            var pmIds = parseIds(filters.getProjectManagerId());
            if (!pmIds.isEmpty()) {
                where = where.and(CP.UTL_ID.in(pmIds));
            }

            // 4. Management assistant (optional)
            var maIds = parseIds(filters.getManagementAssistantId());
            if (!maIds.isEmpty()) {
                var AMP_AG = Tables.AUTRE_MEMBRE_PROJET.as("amp_ag");
                query = query.join(AMP_AG).on(D.DOS_ID.eq(AMP_AG.DOS_ID));
                where = where.and(AMP_AG.AMP_AG.eq(DSL.inline(FLAG_TRUE)))
                             .and(AMP_AG.UTL_ID.in(maIds));
            }

            // 5. Sensitivity (optional)
            if (Boolean.TRUE.equals(filters.getIsSensitive())) {
                var AMP_SENS = Tables.AUTRE_MEMBRE_PROJET.as("amp_sensible");
                query = query.join(AMP_SENS).on(D.DOS_ID.eq(AMP_SENS.DOS_ID));
                where = where.and(AMP_SENS.UTL_ID.eq(DSL.inline(SENSIBLE_USER_ID)))
                             .and(AMP_SENS.AMP_AUT.eq(DSL.inline(SENSIBLE_AUT_FLAG)));
            }

            // 6. Typologies (optional)
            if (filters.getProjectsTypes() != null && !filters.getProjectsTypes().isEmpty()) {
                var RTDE = Tables.REF_TYPE_DOSSIER_EVAL.as("rtde");
                query = query.join(RTDE).on(RTDE.TDE_CODE.eq(D.TDE_CODE));
                where = where.and(RTDE.REGROUP.in(filters.getProjectsTypes()));
            }

            // 7. Finished projects (optional)
            if (Boolean.TRUE.equals(filters.getIsFinished())) {
                where = where.and(D.DOS_DATE_CLOTURE.isNotNull());
            }

            // 8. Phase (optional)
            if (filters.getPhase() != null) {
                String phaseCode = mapPhaseCode(filters.getPhase());
                if (phaseCode != null) {
                    where = where.and(D.AVC_CODE.eq(phaseCode));
                }
            }

            return query.where(where).fetchOne(0, int.class);
        } catch (Exception e) {
            log.error("Failed to count projects with filters {}", filters, e);
            throw new IllegalStateException("Erreur lors du comptage des projets", e);
        }
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
     * Calculates the average time (in days) taken to complete projects, grouped by their typology.
     * The calculation considers the total duration from the start of the project to its closure,
     * minus any suspension periods.
     *
     * @return a map where the keys are typology labels and the values are the average completion times in days
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

    // -------------------- Helpers --------------------
    private Condition buildUserTypeCondition(UserTypeDto userType,
                                             Field<String> tdosCode,
                                             Field<Short> cpjEvalue,
                                             Field<String> srvCode) {
        if (userType == null) {
            throw new IllegalArgumentException("Le type d'utilisateur est obligatoire");
        }
        Condition base = cpjEvalue.eq(DSL.inline(FLAG_TRUE));
        switch (userType) {
            case SBP_SERVICE_MANAGER:
                return base
                        .and(tdosCode.in("RECO", "APP"))
                        .and(srvCode.eq(SBP_SERVICE_CODE));
            case SR_SERVICE_MANAGER:
                return base
                        .and(tdosCode.eq("SMS"))
                        .and(srvCode.eq(SR_SERVICE_CODE));
            default:
                // Par défaut, appliquer uniquement le flag évalue
                return base;
        }
    }

    @SafeVarargs
    private final Condition buildPeriodCondition(LocalDate beginDate, LocalDate endDate, Field<LocalDateTime>... dateFields) {
        if (beginDate == null || endDate == null) {
            return DSL.noCondition();
        }
        LocalDateTime begin = beginDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay().minusNanos(1);
        Condition ge = DSL.falseCondition();
        Condition le = DSL.falseCondition();
        for (Field<LocalDateTime> f : dateFields) {
            ge = ge.or(f.ge(begin));
            le = le.or(f.le(end));
        }
        return ge.and(le);
    }

    private List<Long> parseIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        List<Long> out = new ArrayList<>();
        for (String s : ids) {
            if (s == null || s.isBlank()) continue;
            try {
                out.add(Long.valueOf(s.trim()));
            } catch (NumberFormatException e) {
                log.warn("Ignoring invalid numeric id: {}", s);
            }
        }
        return out;
    }

    private String mapPhaseCode(ProjectPhaseFilterDto phase) {
        if (phase == null) return null;
        String name = phase.name();
        switch (name) {
            case "SAISINE":
                return "0-DEBUT_SAISINE";
            case "DEBUT_CADRAGE":
                return "1-DEBUT_CADRAGE";
            case "CADRAGE":
                return "2-CADRAGE";
            case "PGT":
                return "3-PER_GT";
            case "DGT":
                return "4-DERNIER_GT";
            case "EXAMEN":
                return "5-Examen";
            case "VALIDATION":
                return "6-VALIDATION";
            case "MISE_EN_LIGNE":
                return "7-DIFFUSION";
            case "CLOTURE":
                return "8-CLOTURE";
            default:
                return null;
        }
    }


}
