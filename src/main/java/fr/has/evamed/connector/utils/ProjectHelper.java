package fr.has.evamed.connector.utils;

import fr.has.evamed.connector.domain.*;
import fr.has.evamed.domain.entities.Tables;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static fr.has.evamed.connector.utils.EvamedConstants.*;
import static fr.has.evamed.connector.utils.EvamedConstants.ORANGE_PHASE_LABEL;
import static fr.has.evamed.connector.utils.EvamedConstants.VIOLET_PHASE_LABEL;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectHelper {

    @NonNull
    private final DSLContext context;

    public void enrichProjects(List<ProjectDto> items) {
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

        populatePhases(items);

        // Ensure non-null group objects/lists according to contract
        for (ProjectDto dto : items) {
            if (dto.getProjectManagers() == null) {
                dto.setProjectManagers(new ProjectProjectManagersDto());
            }
            if (dto.getPhases() == null) {
                dto.setPhases(List.of());
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

    private void populatePhases(List<ProjectDto> items) {
        if (items == null || items.isEmpty()) {
            return;
        }
        for (ProjectDto dto : items) {
            if (dto == null) {
                continue;
            }
            List<PhaseDto> phases = new ArrayList<>();

            addPhase(phases, BLUE_PHASE_LABEL, dto.getScopingStartDate(), dto.getScopingDate());

            List<GtGlMeetingDto> meetings = dto.getGtglMeetings();
            LocalDate firstMeetingDate = null;
            LocalDate lastMeetingDate = null;
            if (meetings != null && !meetings.isEmpty()) {
                firstMeetingDate = meetings.stream()
                        .map(GtGlMeetingDto::getDate)
                        .filter(Objects::nonNull)
                        .min(Comparator.naturalOrder())
                        .orElse(null);
                lastMeetingDate = meetings.stream()
                        .map(GtGlMeetingDto::getDate)
                        .filter(Objects::nonNull)
                        .max(Comparator.naturalOrder())
                        .orElse(null);
            }

            LocalDate orangeStart = dto.getScopingDate() != null ? dto.getScopingDate() : firstMeetingDate;
            addPhase(phases, ORANGE_PHASE_LABEL, orangeStart, lastMeetingDate);

            addPhase(phases, VIOLET_PHASE_LABEL, dto.getReviewDate(), dto.getValidationDate());

            dto.setPhases(phases);
        }
    }

    private void addPhase(List<PhaseDto> phases, String label, LocalDate begin, LocalDate end) {
        if (begin == null && end == null) {
            return;
        }
        PhaseDto phase = new PhaseDto();
        phase.setLabel(label);
        if (begin != null) {
            phase.setBeginDate(begin);
        }
        if (end != null) {
            phase.setEndDate(end);
        }
        phases.add(phase);
    }

    @SafeVarargs
    public final Condition buildPeriodCondition(LocalDate beginDate, LocalDate endDate, Field<LocalDateTime>... dateFields) {
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

    public List<Long> parseIds(List<String> ids) {
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

    public String mapPhaseCode(ProjectPhaseFilterDto phase) {
        if (phase == null) return null;
        String name = phase.name();
        return switch (name) {
            case "SAISINE" -> "0-DEBUT_SAISINE";
            case "DEBUT_CADRAGE" -> "1-DEBUT_CADRAGE";
            case "CADRAGE" -> "2-CADRAGE";
            case "PGT" -> "3-PER_GT";
            case "DGT" -> "4-DERNIER_GT";
            case "EXAMEN" -> "5-Examen";
            case "VALIDATION" -> "6-VALIDATION";
            case "MISE_EN_LIGNE" -> "7-DIFFUSION";
            case "CLOTURE" -> "8-CLOTURE";
            default -> null;
        };
    }
}
