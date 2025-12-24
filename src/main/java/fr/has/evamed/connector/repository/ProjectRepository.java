package fr.has.evamed.connector.repository;

import fr.has.evamed.connector.domain.*;
import fr.has.evamed.connector.mapper.ProjectRecordMapper;
import fr.has.evamed.connector.utils.ProjectHelper;
import fr.has.evamed.domain.entities.Tables;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static fr.has.evamed.connector.utils.LogUtils.logActiveFilters;
import static fr.has.evamed.connector.utils.EvamedConstants.*;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.selectCount;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ProjectRepository {

    @NonNull
    private final DSLContext context;
    private final ProjectHelper projectHelper;


    /**
     * Retrieves a list of projects with pagination support.
     *
     * @param offset the starting index for the result set, used for pagination
     * @param limit the maximum number of projects to retrieve
     * @return a list of ProjectDto objects representing the projects
     */
    public List<ProjectDto> getProjects(Integer offset, Integer limit, ProjectFiltersDto filters) {
        log.info("Requesting database for projects and applying filters ...");
        logActiveFilters(filters);
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
                        projectHelper.buildPeriodCondition(
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
            var pmIds = projectHelper.parseIds(filters.getProjectManagersIds());
            if (!pmIds.isEmpty()) {
                where = where.and(Tables.CHEF_PROJET.UTL_ID.in(pmIds));
            }

            // 4. Management assistant (optional)
            var maIds = projectHelper.parseIds(filters.getManagementAssistantsIds());
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
            if (Boolean.TRUE.equals(filters.getIsProjectFinished())) {
                where = where.and(Tables.DOSSIER.DOS_DATE_CLOTURE.isNotNull());
            }

            // 8. Phase (optional)
            if (filters.getPhase() != null) {
                String phaseCode = projectHelper.mapPhaseCode(filters.getPhase());
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
            projectHelper.enrichProjects(items);
            return items;
        } catch (Exception e) {
            log.error("Failed to fetch projects with filters {}", filters, e);
            throw new IllegalStateException("Erreur lors de la récupération des projets", e);
        }
    }

    public int countProjects(ProjectFiltersDto filters) {
        log.info("Requesting database for counting projects using filters ...");
        logActiveFilters(filters);
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
                        projectHelper.buildPeriodCondition(
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
            var pmIds = projectHelper.parseIds(filters.getProjectManagersIds());
            if (!pmIds.isEmpty()) {
                where = where.and(CP.UTL_ID.in(pmIds));
            }

            // 4. Management assistant (optional)
            var maIds = projectHelper.parseIds(filters.getManagementAssistantsIds());
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
            if (Boolean.TRUE.equals(filters.getIsProjectFinished())) {
                where = where.and(D.DOS_DATE_CLOTURE.isNotNull());
            }

            // 8. Phase (optional)
            if (filters.getPhase() != null) {
                String phaseCode = projectHelper.mapPhaseCode(filters.getPhase());
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

    public List<ProjectManagerDto> getProjectManagers(UserTypeDto userType) {
        log.info("Requesting database for project managers with userType {} ...", userType);
        var U = Tables.UTILISATEUR.as("u");

        Condition condition = U.UTL_ARCHIVE.eq((short) 0);
        if (UserTypeDto.SBP_SERVICE_MANAGER.equals(userType)) {
            condition = condition
                    .and(U.PRF_CODE.eq(SBP_MANAGER_PROFILE_CODE))
                    .and(U.SRV_CODE.eq(SBP_SERVICE_CODE));
        } else if (UserTypeDto.SR_SERVICE_MANAGER.equals(userType)) {
            condition = condition
                    .and(U.PRF_CODE.eq(SR_MANAGER_PROFILE_CODE))
                    .and(U.SRV_CODE.eq(SR_SERVICE_CODE));
        } else {
            throw new IllegalArgumentException("Unsupported user type for project managers: " + userType);
        }

        var records = context
                .selectDistinct(U.UTL_ID, U.UTL_NOM, U.UTL_PRENOM)
                .from(U)
                .where(condition)
                .orderBy(U.UTL_NOM.asc(), U.UTL_PRENOM.asc())
                .fetch();

        List<ProjectManagerDto> managers = new ArrayList<>();
        records.forEach(r -> {
            ProjectManagerDto dto = new ProjectManagerDto();
            var id = r.get(U.UTL_ID);
            if (id != null) dto.setId(String.valueOf(id));
            var firstName = r.get(U.UTL_PRENOM);
            if (firstName != null) dto.setFirstName(firstName);
            var lastName = r.get(U.UTL_NOM);
            if (lastName != null) dto.setLastName(lastName);
            managers.add(dto);
        });
        return managers;
    }

    public List<ManagementAssistantDto> getManagementAssistants(UserTypeDto userType) {
        log.info("Requesting database for management assistants with userType {} ...", userType);
        var U = Tables.UTILISATEUR.as("u");

        Condition condition = U.UTL_ARCHIVE.eq((short) 0);
        if (UserTypeDto.SBP_SERVICE_MANAGER.equals(userType)) {
            condition = condition
                    .and(U.PRF_CODE.eq(SBP_ASSISTANT_PROFILE_CODE))
                    .and(U.SRV_CODE.eq(SBP_SERVICE_CODE));
        } else if (UserTypeDto.SR_SERVICE_MANAGER.equals(userType)) {
            condition = condition
                    .and(U.PRF_CODE.eq(SR_ASSISTANT_PROFILE_CODE))
                    .and(U.SRV_CODE.eq(SR_SERVICE_CODE));
        } else {
            throw new IllegalArgumentException("Unsupported user type for management assistants: " + userType);
        }

        var records = context
                .selectDistinct(U.UTL_ID, U.UTL_NOM, U.UTL_PRENOM)
                .from(U)
                .where(condition)
                .orderBy(U.UTL_NOM.asc(), U.UTL_PRENOM.asc())
                .fetch();

        List<ManagementAssistantDto> assistants = new ArrayList<>();
        records.forEach(r -> {
            ManagementAssistantDto dto = new ManagementAssistantDto();
            var id = r.get(U.UTL_ID);
            if (id != null) dto.setId(String.valueOf(id));
            var firstName = r.get(U.UTL_PRENOM);
            if (firstName != null) dto.setFirstName(firstName);
            var lastName = r.get(U.UTL_NOM);
            if (lastName != null) dto.setLastName(lastName);
            assistants.add(dto);
        });
        return assistants;
    }

    public List<String> getTypologies(UserTypeDto userType) {
        log.info("Requesting database for typologies with userType {} ...", userType);
        var D = Tables.DOSSIER.as("d");
        var RTDE = Tables.REF_TYPE_DOSSIER_EVAL.as("rtde");

        Condition condition = DSL.noCondition();
        if (UserTypeDto.SBP_SERVICE_MANAGER.equals(userType)) {
            condition = condition.and(D.TDOS_CODE.in("RECO", "APP"));
        } else if (UserTypeDto.SR_SERVICE_MANAGER.equals(userType)) {
            condition = condition.and(D.TDOS_CODE.eq("SMS"));
        } else {
            throw new IllegalArgumentException("Unsupported user type for typologies: " + userType);
        }

        condition = condition
                .and(RTDE.REGROUP.isNotNull())
                .and(D.DOS_DATE_SUP_LOG.isNull());

        return context
                .selectDistinct(RTDE.REGROUP)
                .from(RTDE.join(D).on(RTDE.TDE_CODE.eq(D.TDE_CODE)))
                .where(condition)
                .orderBy(RTDE.REGROUP.asc())
                .fetch(RTDE.REGROUP);
    }

    public List<ManagementAssistantDto> getProjectManagementAssistantsByFilter(ProjectFiltersDto filters) {
        log.info("Requesting database for project management assistants by filters ...");
        logActiveFilters(filters);
        try {
            var D = Tables.DOSSIER.as("d");
            var CP = Tables.CHEF_PROJET.as("cp");
            var U_PM = Tables.UTILISATEUR.as("u_pm");
            var AMP_AG = Tables.AUTRE_MEMBRE_PROJET.as("amp_ag");
            var U_AG = Tables.UTILISATEUR.as("u_ag");

            var query = context
                    .selectDistinct(U_AG.UTL_ID, U_AG.UTL_NOM, U_AG.UTL_PRENOM)
                    .from(D)
                    .join(CP).on(D.DOS_ID.eq(CP.DOS_ID))
                    .join(U_PM).on(CP.UTL_ID.eq(U_PM.UTL_ID))
                    .join(AMP_AG).on(D.DOS_ID.eq(AMP_AG.DOS_ID))
                    .join(U_AG).on(AMP_AG.UTL_ID.eq(U_AG.UTL_ID));

            Condition where = DSL.noCondition();

            // 1. User type (mandatory)
            where = where.and(buildUserTypeCondition(filters.getUserType(), D.TDOS_CODE, CP.CPJ_EVALUE, U_PM.SRV_CODE));

            // 2. Period (optional)
            if (filters.getPeriod() != null && filters.getPeriod().getBeginDate() != null && filters.getPeriod().getEndDate() != null) {
                where = where.and(
                        projectHelper.buildPeriodCondition(
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
            var pmIds = projectHelper.parseIds(filters.getProjectManagersIds());
            if (!pmIds.isEmpty()) {
                where = where.and(CP.UTL_ID.in(pmIds));
            }

            // 4. Management assistant (optional) + mandatory AMP_AG flag
            where = where.and(AMP_AG.AMP_AG.eq(DSL.inline(FLAG_TRUE)));
            var maIds = projectHelper.parseIds(filters.getManagementAssistantsIds());
            if (!maIds.isEmpty()) {
                where = where.and(AMP_AG.UTL_ID.in(maIds));
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
            if (Boolean.TRUE.equals(filters.getIsProjectFinished())) {
                where = where.and(D.DOS_DATE_CLOTURE.isNotNull());
            }

            // 8. Phase (optional)
            if (filters.getPhase() != null) {
                String phaseCode = projectHelper.mapPhaseCode(filters.getPhase());
                if (phaseCode != null) {
                    where = where.and(D.AVC_CODE.eq(phaseCode));
                }
            }

            var records = query
                    .where(where)
                    .orderBy(U_AG.UTL_NOM.asc(), U_AG.UTL_PRENOM.asc())
                    .fetch();

            List<ManagementAssistantDto> assistants = new ArrayList<>();
            records.forEach(r -> {
                ManagementAssistantDto dto = new ManagementAssistantDto();
                var id = r.get(U_AG.UTL_ID);
                if (id != null) dto.setId(String.valueOf(id));
                var firstName = r.get(U_AG.UTL_PRENOM);
                if (firstName != null) dto.setFirstName(firstName);
                var lastName = r.get(U_AG.UTL_NOM);
                if (lastName != null) dto.setLastName(lastName);
                assistants.add(dto);
            });
            return assistants;
        } catch (Exception e) {
            log.error("Failed to fetch management assistants by filters {}", filters, e);
            throw new IllegalStateException("Erreur lors de la récupération des assistantes de gestion", e);
        }
    }

    public List<ProjectManagerDto> getProjectsManagersByFilter(ProjectFiltersDto filters) {
        log.info("Requesting database for project managers by filters ...");
        logActiveFilters(filters);
        try {
            var D = Tables.DOSSIER.as("d");
            var CP = Tables.CHEF_PROJET.as("cp");
            var U = Tables.UTILISATEUR.as("u");

            var query = context
                    .selectDistinct(U.UTL_ID, U.UTL_NOM, U.UTL_PRENOM)
                    .from(D)
                    .join(CP).on(D.DOS_ID.eq(CP.DOS_ID))
                    .join(U).on(CP.UTL_ID.eq(U.UTL_ID));

            Condition where = DSL.noCondition();

            // 1. User type (mandatory)
            where = where.and(buildUserTypeCondition(filters.getUserType(), D.TDOS_CODE, CP.CPJ_EVALUE, U.SRV_CODE));

            // 2. Period (optional)
            if (filters.getPeriod() != null && filters.getPeriod().getBeginDate() != null && filters.getPeriod().getEndDate() != null) {
                where = where.and(
                        projectHelper.buildPeriodCondition(
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
            var pmIds = projectHelper.parseIds(filters.getProjectManagersIds());
            if (!pmIds.isEmpty()) {
                where = where.and(CP.UTL_ID.in(pmIds));
            }

            // 4. Management assistant (optional)
            var maIds = projectHelper.parseIds(filters.getManagementAssistantsIds());
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
            if (Boolean.TRUE.equals(filters.getIsProjectFinished())) {
                where = where.and(D.DOS_DATE_CLOTURE.isNotNull());
            }

            // 8. Phase (optional)
            if (filters.getPhase() != null) {
                String phaseCode = projectHelper.mapPhaseCode(filters.getPhase());
                if (phaseCode != null) {
                    where = where.and(D.AVC_CODE.eq(phaseCode));
                }
            }

            var records = query
                    .where(where)
                    .orderBy(U.UTL_NOM.asc(), U.UTL_PRENOM.asc())
                    .fetch();

            List<ProjectManagerDto> managers = new ArrayList<>();
            records.forEach(r -> {
                ProjectManagerDto dto = new ProjectManagerDto();
                var id = r.get(U.UTL_ID);
                if (id != null) dto.setId(String.valueOf(id));
                var firstName = r.get(U.UTL_PRENOM);
                if (firstName != null) dto.setFirstName(firstName);
                var lastName = r.get(U.UTL_NOM);
                if (lastName != null) dto.setLastName(lastName);
                managers.add(dto);
            });
            return managers;
        } catch (Exception e) {
            log.error("Failed to fetch project managers by filters {}", filters, e);
            throw new IllegalStateException("Error while fetching project managers using filters", e);
        }
    }

    // -------------------- Helpers --------------------
    private Condition buildUserTypeCondition(UserTypeDto userType,
                                             Field<String> tdosCode,
                                             Field<Short> cpjEvalue,
                                             Field<String> srvCode) {
        if (userType == null) {
            throw new IllegalArgumentException("User type must be provided" );
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


}
