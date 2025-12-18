package fr.has.evamed.connector.repository;

import fr.has.evamed.connector.domain.TypologyPerDomainGlobalIndicatorResponseDto;
import fr.has.evamed.connector.domain.UserTypeDto;
import fr.has.evamed.domain.entities.Tables;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static fr.has.evamed.connector.utils.EvamedConstants.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class GlobalIndicatorsRepository {

    @NonNull private final DSLContext context;

    // Local fallbacks to avoid cross-package coupling issues during compilation


    public Map<String, Integer> getProjectsByTypology(UserTypeDto userType) {
        log.info("Requesting database for projects by typology ...");
        var D = Tables.DOSSIER.as("d");
        var CP = Tables.CHEF_PROJET.as("cp");
        var U = Tables.UTILISATEUR.as("u");
        var RTDE = Tables.REF_TYPE_DOSSIER_EVAL.as("rtde");

        // Build base WHERE according to user type
        Condition where = buildUserTypeCondition(userType, D.TDOS_CODE, CP.CPJ_EVALUE, U.SRV_CODE);

        // Filtrage par date pour l'année en cours
        LocalDate now = LocalDate.now();
        LocalDate beginDate = now.withDayOfYear(1);
        LocalDate endDate = now.withMonth(12).withDayOfMonth(31);
        where = where.and(
                buildPeriodCondition(
                        beginDate,
                        endDate,
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

        var occurences = DSL.coalesce(DSL.countDistinct(D.DOS_ID), 0).as("occurences");

        var result = context
                .select(RTDE.REGROUP, occurences)
                .from(D
                        .join(CP).on(D.DOS_ID.eq(CP.DOS_ID))
                        .join(U).on(CP.UTL_ID.eq(U.UTL_ID))
                        .leftJoin(RTDE).on(RTDE.TDE_CODE.eq(D.TDE_CODE))
                )
                .where(where)
                .groupBy(RTDE.REGROUP)
                .fetch();

        Map<String, Integer> map = new HashMap<>();
        result.forEach(rec -> {
            String key = rec.get(RTDE.REGROUP);
            Integer value = rec.get("occurences", Integer.class);
            map.put(key != null ? key : DEFAULT_LABEL, value != null ? value : 0);
        });
        return map;
    }

    public Map<String, Integer> getProjectsByPhase(UserTypeDto userType) {
        log.info("Requesting database for projects by phase ...");
        var D = Tables.DOSSIER.as("d");
        var CP = Tables.CHEF_PROJET.as("cp");
        var U = Tables.UTILISATEUR.as("u");
        var RAD = Tables.REF_AVANCEMENT_DOSSIER.as("rad");

        // Base condition by user type (shared logic)
        Condition where = buildUserTypeCondition(userType, D.TDOS_CODE, CP.CPJ_EVALUE, U.SRV_CODE);

        // Filtrage par date pour l'année en cours
        LocalDate now = LocalDate.now();
        LocalDate beginDate = now.withDayOfYear(1);
        LocalDate endDate = now.withMonth(12).withDayOfMonth(31);
        /*where = where.and(
                buildPeriodCondition(
                        beginDate,
                        endDate,
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
        );*/

        var occurences = DSL.coalesce(DSL.countDistinct(D.DOS_ID), 0).as(OCCURENCES);

        var result = context
                //.select(RAD.AVC_LIBELLE, occurences)
                .select(RAD.AVC_CODE, occurences)
                .from(D
                        .join(CP).on(D.DOS_ID.eq(CP.DOS_ID))
                        .join(U).on(CP.UTL_ID.eq(U.UTL_ID))
                        .leftJoin(RAD).on(RAD.AVC_CODE.eq(D.AVC_CODE))
                )
                .where(where)
                // Group by code and libelle to satisfy SQL standards
                .groupBy(RAD.AVC_CODE, RAD.AVC_LIBELLE)
                .fetch();

        Map<String, Integer> map = new HashMap<>();
        result.forEach(rec -> {
            //String key = rec.get(RAD.AVC_LIBELLE);
            String key = rec.get(RAD.AVC_CODE);
            Integer value = rec.get(OCCURENCES, Integer.class);
            map.put(key != null ? key : DEFAULT_LABEL, value != null ? value : 0);
        });
        return map;
    }

    /**
     * Calculates the average time (in days) taken to complete projects, grouped by their typology.
     * The calculation considers the total duration from the start of the project to its closure,
     * minus any suspension periods.
     *
     * @return a map where the keys are typology labels and the values are the average completion times in days
     */
    public Map<String, Integer> getAverageTimePerTypology(UserTypeDto userType) {
        log.info("Requesting database for calculating average time per typology ...");
        var D = Tables.DOSSIER.as("d");
        var CP = Tables.CHEF_PROJET.as("cp");
        var U = Tables.UTILISATEUR.as("u");
        var RTDE = Tables.REF_TYPE_DOSSIER_EVAL.as("rtde");

        // 1) Base condition selon le type d'utilisateur (SBP/SR)
        Condition where = buildUserTypeCondition(userType, D.TDOS_CODE, CP.CPJ_EVALUE, U.SRV_CODE);

        // 2) Filtres spécifiques à la requête
        where = where
                .and(D.MCO_CODE.in(ALLOWED_MCO_CODES))
                .and(D.DOS_DATE_SUP_LOG.isNull())
                .and(D.DOS_DENORM_DELAI_ECOULE.gt(DSL.inline(0)));

        // 3) Filtrage par date de clôture sur l'année courante
        LocalDate now = LocalDate.now();
        LocalDate beginDate = now.withDayOfYear(1);
        LocalDate endDate = now.withMonth(12).withDayOfMonth(31);
        where = where.and(buildPeriodCondition(beginDate, endDate, D.DOS_DATE_CLOTURE));

        // 4) Agrégat: round(avg(d.dos_denorm_delai_ecoule), 0)
        Field<Integer> delaiMoyen = DSL.round(DSL.avg(D.DOS_DENORM_DELAI_ECOULE), 0)
                .cast(Integer.class)
                .as(COL_DELAI_MOYEN);

        var records = context
                .select(RTDE.REGROUP, delaiMoyen)
                .from(D
                        .join(CP).on(D.DOS_ID.eq(CP.DOS_ID))
                        .join(U).on(CP.UTL_ID.eq(U.UTL_ID))
                        .leftJoin(RTDE).on(D.TDE_CODE.eq(RTDE.TDE_CODE))
                )
                .where(where)
                .groupBy(RTDE.REGROUP)
                .orderBy(RTDE.REGROUP.asc().nullsFirst())
                .fetch();

        Map<String, Integer> map = new HashMap<>();
        records.forEach(rec -> {
            String key = rec.get(RTDE.REGROUP);
            Integer value = rec.get(COL_DELAI_MOYEN, Integer.class);
            map.put(key != null ? key : DEFAULT_LABEL, value != null ? value : 0);
        });
        return map;
    }

    public List<TypologyPerDomainGlobalIndicatorResponseDto> getProjectTypologyPerDomain(UserTypeDto userType) {
        log.info("Requesting database for project typology per domain ...");
        var D = Tables.DOSSIER.as("d");
        var CP = Tables.CHEF_PROJET.as("cp");
        var U = Tables.UTILISATEUR.as("u");
        var DDA = Tables.DOSSIER_DOMAINES_APPLICATION.as("dda");
        var RDA = Tables.REF_DOMAINES_APPLICATION.as("rda");
        var RTDE = Tables.REF_TYPE_DOSSIER_EVAL.as("rtde");

        // Base condition by user type (shared logic)
        Condition where = buildUserTypeCondition(userType, D.TDOS_CODE, CP.CPJ_EVALUE, U.SRV_CODE);

        // Filtrage par date pour l'année en cours
        LocalDate now = LocalDate.now();
        LocalDate beginDate = now.withDayOfYear(1);
        LocalDate endDate = now.withMonth(12).withDayOfMonth(31);
        where = where.and(
                buildPeriodCondition(
                        beginDate,
                        endDate,
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

        var occurences = DSL.coalesce(DSL.countDistinct(D.DOS_ID), 0).as(OCCURENCES);

        var records = context
                .select(RDA.DAP_LIBELLE, RTDE.REGROUP, occurences)
                .from(D
                        .join(CP).on(D.DOS_ID.eq(CP.DOS_ID))
                        .join(U).on(CP.UTL_ID.eq(U.UTL_ID))
                        .leftJoin(DDA).on(D.DOS_ID.eq(DDA.DOS_ID))
                        .leftJoin(RDA).on(DDA.DAP_CODE.eq(RDA.DAP_CODE))
                        .leftJoin(RTDE).on(D.TDE_CODE.eq(RTDE.TDE_CODE))
                )
                .where(where)
                .groupBy(RDA.DAP_LIBELLE, RTDE.REGROUP)
                .orderBy(RDA.DAP_LIBELLE.asc().nullsFirst(), RTDE.REGROUP.asc().nullsFirst())
                .fetch();

        // Le schéma courant expose un seul objet. Nous renvoyons la première ligne ordonnée,
        // en appliquant les valeurs par défaut si nécessaire.
        if (records.isEmpty()) {
            log.warn("No data found for project typology per domain");
            return Collections.emptyList();
        }

        return records.stream()
                .map(record -> new TypologyPerDomainGlobalIndicatorResponseDto(
                        record.get(RDA.DAP_LIBELLE),
                        record.get(RTDE.REGROUP),
                        record.get(OCCURENCES, Integer.class)
                ))
                .toList();
    }

    public String getWorkingGroupForCurrentYear(UserTypeDto userType){
        log.info("Requesting database for working group for current year ...");
        var GTR = Tables.GROUPE_TRAVAIL_REUNION.as("gtr");
        var GT = Tables.GROUPE_TRAVAIL.as("gt");
        var OJGT = Tables.ORDRE_JOUR_GROUPE_TRAVAIL.as("ojgt");
        var D = Tables.DOSSIER.as("d");
        var CP = Tables.CHEF_PROJET.as("cp");
        var U = Tables.UTILISATEUR.as("u");

        // Base condition: user type (tdos_code + srv_code + cpj_evalue)
        Condition where = buildUserTypeCondition(userType, D.TDOS_CODE, CP.CPJ_EVALUE, U.SRV_CODE)
                .and(GT.TGE_CODE.eq("GDT"));

        // Period filter for the current year on meeting date (gtr.gre_date)
        LocalDate now = LocalDate.now();
        LocalDate beginDate = now.withDayOfYear(1);
        LocalDate endDate = now.withMonth(12).withDayOfMonth(31);
        where = where.and(buildPeriodCondition(beginDate, endDate, GTR.GRE_DATE));

        Integer count = context
                .select(DSL.countDistinct(GTR.GRE_ID).as("nb"))
                .from(GTR
                        .join(GT).on(GTR.GTR_ID.eq(GT.GTR_ID))
                        .join(OJGT).on(GT.GTR_ID.eq(OJGT.GTR_ID))
                        .join(D).on(OJGT.DOS_ID.eq(D.DOS_ID))
                        .join(CP).on(D.DOS_ID.eq(CP.DOS_ID))
                        .join(U).on(CP.UTL_ID.eq(U.UTL_ID))
                )
                .where(where)
                .fetchOne(0, Integer.class);

        // Return as string; if null, use default label
        return count == null ? DEFAULT_LABEL : String.valueOf(count);
    }

    // -------------------- Helpers --------------------
    @SafeVarargs
    private final Condition buildPeriodCondition(LocalDate beginDate, LocalDate endDate, Field<LocalDateTime>... dateFields) {
        if (beginDate == null || endDate == null) {
            return DSL.noCondition();
        }
        var begin = beginDate.atStartOfDay();
        var end = endDate.plusDays(1).atStartOfDay().minusNanos(1);
        Condition ge = DSL.falseCondition();
        Condition le = DSL.falseCondition();
        for (Field<LocalDateTime> f : dateFields) {
            ge = ge.or(f.ge(begin));
            le = le.or(f.le(end));
        }
        return ge.and(le);
    }

    private Condition buildUserTypeCondition(UserTypeDto userType,
                                             Field<String> tdosCode,
                                             Field<Short> cpjEvalue,
                                             Field<String> srvCode) {
        if (userType == null) {
            throw new IllegalArgumentException("User type must be provided");
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
                throw new IllegalArgumentException("Unsupported user type: " + userType);
        }
    }
}
