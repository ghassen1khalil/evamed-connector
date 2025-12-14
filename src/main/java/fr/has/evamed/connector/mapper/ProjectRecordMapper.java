package fr.has.evamed.connector.mapper;

import fr.has.evamed.connector.domain.ProjectDto;
import org.apache.logging.log4j.util.Strings;
import org.jooq.Record;
import org.jooq.RecordMapper;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.name;
import org.jooq.Field;
import java.time.LocalDate;

/**
 * Maps a jOOQ Record originating from evamed.dossier to ProjectDto.
 * Explicitly declares the fields to avoid fragile index-based access.
 */
public class ProjectRecordMapper implements RecordMapper<Record, ProjectDto> {

    public static final Field<String> DOS_ID = field(name("evamed", "dossier", "dos_id"), String.class);
    public static final Field<Integer> DOS_NUMERO = field(name("evamed", "dossier", "dos_numero"), Integer.class);
    public static final Field<String> DOS_TITRE = field(name("evamed", "dossier", "dos_titre"), String.class);
    public static final Field<String> TDOS_CODE = field(name("evamed", "dossier", "tdos_code"), String.class);
    public static final Field<String> PLA_COM = field(name("evamed", "dossier", "pla_com"), String.class);

    public static final Field<LocalDate> DOS_DATE_SAISINE = field(name("evamed", "dossier", "dos_date_saisine"), LocalDate.class);
    public static final Field<LocalDate> DOS_DATE_DEBUT_CADRAGE = field(name("evamed", "dossier", "dos_date_debut_cadrage"), LocalDate.class);
    public static final Field<LocalDate> DOS_DATE_CADRAGE = field(name("evamed", "dossier", "dos_date_cadrage"), LocalDate.class);
    public static final Field<LocalDate> DOS_DATE_PGT = field(name("evamed", "dossier", "dos_date_pgt"), LocalDate.class);
    public static final Field<LocalDate> DOS_DATE_DGT = field(name("evamed", "dossier", "dos_date_dgt"), LocalDate.class);
    public static final Field<LocalDate> DOS_DATE_EXAMEN = field(name("evamed", "dossier", "dos_date_examen"), LocalDate.class);
    public static final Field<LocalDate> DOS_DATE_VALIDATION = field(name("evamed", "dossier", "dos_date_validation"), LocalDate.class);
    public static final Field<LocalDate> DOS_DATE_MISE_EN_LIGNE = field(name("evamed", "dossier", "dos_date_mise_en_ligne"), LocalDate.class);
    public static final Field<LocalDate> DOS_DATE_CLOTURE = field(name("evamed", "dossier", "dos_date_cloture"), LocalDate.class);

    // Aliases for computed/functional fields selected in the repository
    public static final Field<LocalDate> PREV_DEBUT_CADRAGE = field(name("prev_debut_cadrage"), LocalDate.class);
    public static final Field<LocalDate> PREV_CADRAGE = field(name("prev_cadrage"), LocalDate.class);
    public static final Field<LocalDate> PREV_PGT = field(name("prev_pgt"), LocalDate.class);
    public static final Field<LocalDate> PREV_DGT = field(name("prev_dgt"), LocalDate.class);
    public static final Field<LocalDate> PREV_EXAMEN = field(name("prev_examen"), LocalDate.class);
    public static final Field<LocalDate> PREV_VALIDATION = field(name("prev_validation"), LocalDate.class);
    public static final Field<LocalDate> PREV_MISE_EN_LIGNE = field(name("prev_mise_en_ligne"), LocalDate.class);
    public static final Field<LocalDate> PREV_CLOTURE = field(name("prev_cloture"), LocalDate.class);
    public static final Field<Integer> SENSIBLE_COUNT = field(name("sensible"), Integer.class);

    @Override
    public ProjectDto map(Record record) {
        String dosId = record.get(DOS_ID);
        Integer dosNumero = record.get(DOS_NUMERO);
        String dosTitre = record.get(DOS_TITRE);

        ProjectDto dto = new ProjectDto();
        if (Strings.isNotBlank(dosId)) {
            dto.setId(dosId);
        }
        if (dosNumero != null) {
            dto.setNumber(String.valueOf(dosNumero));
        }
        if (dosTitre != null) {
            dto.setTitle(dosTitre);
        }
        // Simple fields
        String typeCode = record.get(TDOS_CODE);
        if (typeCode != null) dto.setTypeCode(typeCode);


        // Actual dates
        LocalDate referral = record.get(DOS_DATE_SAISINE);
        if (referral != null) dto.setReferralDate(referral);

        LocalDate scopingStart = record.get(DOS_DATE_DEBUT_CADRAGE);
        if (scopingStart != null) dto.setScopingStartDate(scopingStart);

        LocalDate scoping = record.get(DOS_DATE_CADRAGE);
        if (scoping != null) dto.setScopingDate(scoping);

        LocalDate pgt = record.get(DOS_DATE_PGT);
        if (pgt != null) dto.setPgtDate(pgt);

        LocalDate dgt = record.get(DOS_DATE_DGT);
        if (dgt != null) dto.setDgtDate(dgt);

        LocalDate review = record.get(DOS_DATE_EXAMEN);
        if (review != null) dto.setReviewDate(review);

        LocalDate validation = record.get(DOS_DATE_VALIDATION);
        if (validation != null) dto.setValidationDate(validation);

        LocalDate publication = record.get(DOS_DATE_MISE_EN_LIGNE);
        if (publication != null) dto.setPublicationDate(publication);

        LocalDate closure = record.get(DOS_DATE_CLOTURE);
        if (closure != null) dto.setClosureDate(closure);

        // Planned dates from functions
        LocalDate pScopingStart = record.get(PREV_DEBUT_CADRAGE);
        if (pScopingStart != null) dto.setPlannedScopingStartDate(pScopingStart);

        LocalDate pScoping = record.get(PREV_CADRAGE);
        if (pScoping != null) dto.setPlannedScopingDate(pScoping);

        LocalDate pPgt = record.get(PREV_PGT);
        if (pPgt != null) dto.setPlannedPgtDate(pPgt);

        LocalDate pDgt = record.get(PREV_DGT);
        if (pDgt != null) dto.setPlannedDgtDate(pDgt);

        LocalDate pReview = record.get(PREV_EXAMEN);
        if (pReview != null) dto.setPlannedReviewDate(pReview);

        LocalDate pValidation = record.get(PREV_VALIDATION);
        if (pValidation != null) dto.setPlannedValidationDate(pValidation);

        LocalDate pPublication = record.get(PREV_MISE_EN_LIGNE);
        if (pPublication != null) dto.setPlannedPublicationDate(pPublication);

        LocalDate pClosure = record.get(PREV_CLOTURE);
        if (pClosure != null) dto.setPlannedClosureDate(pClosure);

        // Sensible flag (count > 0)
        Integer sensCount = record.get(SENSIBLE_COUNT);
        if (sensCount != null) {
            dto.setIsSensitive(sensCount > 0);
        }
        return dto;
    }
}
