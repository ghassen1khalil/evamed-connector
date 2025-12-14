package fr.has.evamed.connector.repository;

import fr.has.evamed.connector.domain.ProjectDto;
import fr.has.evamed.connector.domain.CommissionSessionDto;
import fr.has.evamed.connector.domain.GtGlMeetingDto;
import fr.has.evamed.connector.domain.ManagementAssistantDto;
import fr.has.evamed.connector.domain.ProjectManagerDto;
import fr.has.evamed.connector.domain.ProjectProjectManagersDto;
import fr.has.evamed.connector.domain.SuspensionDto;
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
import java.time.format.DateTimeFormatter;
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
    private static final DateTimeFormatter DDMMYYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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

            // Map base projects
            List<ProjectDto> items = records.map(new ProjectRecordMapper());

            // Batch enrichment for lists to avoid N+1
            enrichProjects(items);
            return items;
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

    private void enrichProjects(List<ProjectDto> items) {
        if (items == null || items.isEmpty()) return;
        // Index projects by numeric id
        Map<Long, ProjectDto> byId = new HashMap<>();
        List<Long> ids = new ArrayList<>();
        for (ProjectDto dto : items) {
            try {
                if (dto.getId() != null) {
                    long id = Long.parseLong(dto.getId());
                    byId.put(id, dto);
                    ids.add(id);
                }
            } catch (NumberFormatException ex) {
                log.warn("Skipping non-numeric project id: {}", dto.getId());
            }
        }
        if (ids.isEmpty()) return;

        log.debug("Enriching {} projects in batch", ids.size());

        // Typologies
        Map<Long, List<String>> typologies = loadTypologies(ids);
        typologies.forEach((id, list) -> {
            ProjectDto dto = byId.get(id);
            if (dto != null) {
                List<String> vals = distinctSorted(list);
                if (vals != null) dto.setTypologies(vals);
            }
        });

        // Application domains
        Map<Long, List<String>> appDomains = loadApplicationDomains(ids);
        appDomains.forEach((id, list) -> {
            ProjectDto dto = byId.get(id);
            if (dto != null) setApplicationDomains(dto, distinctSorted(list));
        });

        // Project managers (primary/secondary)
        Map<Long, ProjectProjectManagersDto> managers = loadProjectManagers(ids);
        managers.forEach((id, pm) -> {
            ProjectDto dto = byId.get(id);
            if (dto != null && pm != null) dto.setProjectManagers(pm);
        });

        // Management assistants
        Map<Long, List<ManagementAssistantDto>> assistants = loadManagementAssistants(ids);
        assistants.forEach((id, list) -> {
            ProjectDto dto = byId.get(id);
            if (dto != null && list != null) dto.setManagementAssistant(list);
        });

        // GT/GL meetings
        Map<Long, List<GtGlMeetingDto>> meetings = loadGtGlMeetings(ids);
        meetings.forEach((id, list) -> {
            ProjectDto dto = byId.get(id);
            if (dto != null && list != null) dto.setGtglMeetings(list);
        });

        // Commission sessions
        Map<Long, List<CommissionSessionDto>> sessions = loadCommissionSessions(ids);
        sessions.forEach((id, list) -> {
            ProjectDto dto = byId.get(id);
            if (dto != null && list != null) dto.setCommissionSessions(list);
        });

        // Suspensions
        Map<Long, List<SuspensionDto>> suspensions = loadSuspensions(ids);
        suspensions.forEach((id, list) -> {
            ProjectDto dto = byId.get(id);
            if (dto != null && list != null) dto.setSuspensions(list);
        });

        // Ensure non-null group objects/lists according to contract
        for (ProjectDto dto : items) {
            if (dto.getProjectManagers() == null) {
                dto.setProjectManagers(new ProjectProjectManagersDto());
            }
            // applicationDomains is generated as non-null list in new model; no action needed for lists already initialisées
        }
    }

    private List<String> distinctSorted(List<String> list) {
        if (list == null) return List.of();
        return list.stream().filter(s -> s != null && !s.isBlank()).distinct().sorted().collect(Collectors.toList());
    }

    private void setApplicationDomains(ProjectDto dto, List<String> values) {
        try {
            // Try new API: setApplicationDomains(List<String>)
            var m = dto.getClass().getMethod("setApplicationDomains", List.class);
            m.invoke(dto, values != null ? values : List.of());
        } catch (NoSuchMethodException nsme) {
            // Fallback to legacy single field: join with comma
            try {
                String joined = values == null ? null : String.join(", ", values);
                var m2 = dto.getClass().getMethod("setApplicationDomain", String.class);
                m2.invoke(dto, joined);
            } catch (Exception ignore) {
                log.debug("No applicationDomain(s) setter available on DTO");
            }
        } catch (Exception e) {
            log.warn("Failed to set applicationDomains on DTO id={}", dto.getId(), e);
        }
    }

    private Map<Long, List<String>> loadTypologies(List<Long> ids) {
        var D = Tables.DOSSIER.as("d");
        var RTDE = Tables.REF_TYPE_DOSSIER_EVAL.as("rtde");
        return context
                .select(D.DOS_ID, RTDE.REGROUP)
                .from(RTDE.join(D).on(RTDE.TDE_CODE.eq(D.TDE_CODE)))
                .where(D.DOS_ID.in(ids))
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        r -> r.get(D.DOS_ID),
                        Collectors.mapping(r -> r.get(RTDE.REGROUP), Collectors.toList())
                ));
    }

    private Map<Long, List<String>> loadApplicationDomains(List<Long> ids) {
        var DDA = Tables.DOSSIER_DOMAINES_APPLICATION.as("dda");
        var RDA = Tables.REF_DOMAINES_APPLICATION.as("rda");
        return context
                .select(DDA.DOS_ID, RDA.DAP_LIBELLE)
                .from(DDA.join(RDA).on(DDA.DAP_CODE.eq(RDA.DAP_CODE)))
                .where(DDA.DOS_ID.in(ids))
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        r -> r.get(DDA.DOS_ID),
                        Collectors.mapping(r -> r.get(RDA.DAP_LIBELLE), Collectors.toList())
                ));
    }

    private Map<Long, ProjectProjectManagersDto> loadProjectManagers(List<Long> ids) {
        var CP = Tables.CHEF_PROJET.as("cp");
        var U = Tables.UTILISATEUR.as("u");
        var recs = context
                .select(CP.DOS_ID, U.UTL_ID, U.UTL_NOM, U.UTL_PRENOM, CP.CPJ_RESPONSABLE, CP.CPJ_EVALUE)
                .from(CP.join(U).on(CP.UTL_ID.eq(U.UTL_ID)))
                .where(CP.DOS_ID.in(ids)
                        .and(CP.CPJ_RESPONSABLE.eq(DSL.inline(FLAG_TRUE)).or(CP.CPJ_EVALUE.eq(DSL.inline(FLAG_TRUE)))))
                .fetch();

        Map<Long, ProjectProjectManagersDto> map = new HashMap<>();
        recs.forEach(r -> {
            Long dosId = r.get(CP.DOS_ID);
            String uid = r.get(U.UTL_ID) != null ? String.valueOf(r.get(U.UTL_ID)) : null;
            String nom = r.get(U.UTL_NOM);
            String prenom = r.get(U.UTL_PRENOM);
            Short resp = r.get(CP.CPJ_RESPONSABLE);
            Short eval = r.get(CP.CPJ_EVALUE);

            ProjectManagerDto pm = new ProjectManagerDto();
            if (uid != null) pm.setId(uid);
            if (prenom != null) pm.setFirstName(prenom);
            if (nom != null) pm.setLastName(nom);

            var group = map.computeIfAbsent(dosId, k -> new ProjectProjectManagersDto());
            if (eval != null && eval == FLAG_TRUE) {
                // primary
                var list = group.getPrimary();
                if (list == null) list = new ArrayList<>();
                list.add(pm);
                group.setPrimary(list);
            }
            if (resp != null && resp == FLAG_TRUE) {
                // secondary
                var list = group.getSecondary();
                if (list == null) list = new ArrayList<>();
                list.add(pm);
                group.setSecondary(list);
            }
        });
        return map;
    }

    private Map<Long, List<ManagementAssistantDto>> loadManagementAssistants(List<Long> ids) {
        var AMP = Tables.AUTRE_MEMBRE_PROJET.as("amp");
        var U = Tables.UTILISATEUR.as("u");
        var recs = context
                .select(AMP.DOS_ID, U.UTL_ID, U.UTL_NOM, U.UTL_PRENOM)
                .from(U.join(AMP).on(U.UTL_ID.eq(AMP.UTL_ID)))
                .where(AMP.AMP_AG.eq(DSL.inline(FLAG_TRUE)).and(AMP.DOS_ID.in(ids)))
                .fetch();

        Map<Long, List<ManagementAssistantDto>> map = new HashMap<>();
        recs.forEach(r -> {
            Long dosId = r.get(AMP.DOS_ID);
            String uid = r.get(U.UTL_ID) != null ? String.valueOf(r.get(U.UTL_ID)) : null;
            String nom = r.get(U.UTL_NOM);
            String prenom = r.get(U.UTL_PRENOM);
            ManagementAssistantDto dto = new ManagementAssistantDto();
            if (uid != null) dto.setId(uid);
            if (prenom != null) dto.setFirstName(prenom);
            if (nom != null) dto.setLastName(nom);
            map.computeIfAbsent(dosId, k -> new ArrayList<>()).add(dto);
        });
        return map;
    }

    private Map<Long, List<GtGlMeetingDto>> loadGtGlMeetings(List<Long> ids) {
        var GR = Tables.GROUPE_TRAVAIL_REUNION.as("gr");
        var GT = Tables.GROUPE_TRAVAIL.as("gt");
        var ODJ = Tables.ORDRE_JOUR_GROUPE_TRAVAIL.as("odj");
        var recs = context
                .select(ODJ.DOS_ID, GR.GRE_DATE, GT.TGE_CODE)
                .from(GR
                        .join(GT).on(GR.GTR_ID.eq(GT.GTR_ID))
                        .join(ODJ).on(GT.GTR_ID.eq(ODJ.GTR_ID)))
                .where(ODJ.DOS_ID.in(ids).and(GT.TGE_CODE.in("GDT", "GDL", "CPP", "GTECH")))
                .fetch();

        Map<Long, List<GtGlMeetingDto>> map = new HashMap<>();
        recs.forEach(r -> {
            Long dosId = r.get(ODJ.DOS_ID);
            LocalDateTime dateTime = r.get(GR.GRE_DATE);
            String code = r.get(GT.TGE_CODE);
            String prefix = "";
            if ("GDT".equals(code)) prefix = "GT";
            else if ("GDL".equals(code)) prefix = "GL";
            else if ("CPP".equals(code)) prefix = "CPP";
            else if ("GTECH".equals(code)) prefix = "GTECH";

            LocalDate date = dateTime != null ? dateTime.toLocalDate() : null;
            String label = prefix + (date != null ? (" " + DDMMYYYY.format(date)) : "");

            GtGlMeetingDto dto = new GtGlMeetingDto();
            if (date != null) dto.setDate(date);
            dto.setLabel(label);
            map.computeIfAbsent(dosId, k -> new ArrayList<>()).add(dto);
        });
        return map;
    }

    private Map<Long, List<CommissionSessionDto>> loadCommissionSessions(List<Long> ids) {
        var ODJ = Tables.ELEMENT_ORDRE_JOUR_COMISSION.as("odj");
        var COM = Tables.COMMISSION.as("com");
        var RTP = Tables.REF_TYPE_PASSAGE.as("rtp");
        var recs = context
                .select(ODJ.DOS_ID, COM.COM_DATE_REUNION, COM.TCO_CODE, COM.ISDELIBERATIF, RTP.TPS_LIBELLE)
                .from(ODJ
                        .join(COM).on(COM.COM_ID.eq(ODJ.COM_ID))
                        .join(RTP).on(ODJ.TPS_CODE.eq(RTP.TPS_CODE)))
                .where(ODJ.DOS_ID.in(ids).and(ODJ.COM_ID_REPROG.isNull()))
                .fetch();

        Map<Long, List<CommissionSessionDto>> map = new HashMap<>();
        recs.forEach(r -> {
            Long dosId = r.get(ODJ.DOS_ID);
            LocalDateTime dateTime = r.get(COM.COM_DATE_REUNION);
            LocalDate date = dateTime != null ? dateTime.toLocalDate() : null;
            String tco = r.get(COM.TCO_CODE);
            Short deliberatif = r.get(COM.ISDELIBERATIF);
            String libellePassage = r.get(RTP.TPS_LIBELLE);

            String prefix;
            if ("COLLEGE".equals(tco)) {
                prefix = (deliberatif != null && deliberatif == FLAG_TRUE) ? "CD" : "COI";
            } else if ("CAPPSP".equals(tco)) {
                prefix = "CRPPI";
            } else {
                prefix = tco != null ? tco : "";
            }
            String label = prefix + (libellePassage != null ? ("_" + libellePassage) : "") + (date != null ? (" " + DDMMYYYY.format(date)) : "");

            CommissionSessionDto dto = new CommissionSessionDto();
            if (date != null) dto.setDate(date);
            dto.setLabel(label);
            map.computeIfAbsent(dosId, k -> new ArrayList<>()).add(dto);
        });
        return map;
    }

    private Map<Long, List<SuspensionDto>> loadSuspensions(List<Long> ids) {
        var SD = Tables.SUSPENSION_DELAI.as("sd");
        var RMPS = Tables.REF_MOTIF_PERIODE_SUSPENSION.as("rmps");
        var recs = context
                .select(SD.DOS_ID, SD.SPD_DATE_DE_DEBUT, SD.SPD_DATE_DE_FIN, RMPS.MPS_LIBELLE, SD.SPD_COMMENTAIRE)
                .from(SD.join(RMPS).on(SD.MPS_CODE.eq(RMPS.MPS_CODE)))
                .where(SD.DOS_ID.in(ids))
                .fetch();

        Map<Long, List<SuspensionDto>> map = new HashMap<>();
        recs.forEach(r -> {
            Long dosId = r.get(SD.DOS_ID);
            LocalDateTime d1 = r.get(SD.SPD_DATE_DE_DEBUT);
            LocalDateTime d2 = r.get(SD.SPD_DATE_DE_FIN);
            String lib = r.get(RMPS.MPS_LIBELLE);
            Object comment = r.get(SD.SPD_COMMENTAIRE);
            SuspensionDto dto = new SuspensionDto();
            if (d1 != null) dto.setStartDate(d1.toLocalDate());
            if (d2 != null) dto.setEndDate(d2.toLocalDate());
            dto.setLabel(lib);
            dto.setComment(comment);
            map.computeIfAbsent(dosId, k -> new ArrayList<>()).add(dto);
        });
        return map;
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
