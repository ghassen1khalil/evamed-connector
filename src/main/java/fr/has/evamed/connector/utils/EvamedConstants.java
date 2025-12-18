package fr.has.evamed.connector.utils;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class EvamedConstants {

    // Constants (good practices: no magic numbers/strings inline)
    public static final int SECONDS_PER_DAY = 86_400;
    public static final long SENSIBLE_USER_ID = 9_461L;
    public static final short SENSIBLE_AUT_FLAG = 1;
    public static final short FLAG_TRUE = 1;
    public static final String DEFAULT_LABEL = "Aucun";
    public static final String COL_DELAI_MOYEN = "delai_moyen";
    public static final String SBP_SERVICE_CODE = "DAQSS_SBPP";
    public static final String SR_SERVICE_CODE = "DIQASM_SR";
    public static final String SBP_MANAGER_PROFILE_CODE = "50";
    public static final String SR_MANAGER_PROFILE_CODE = "1004";
    public static final String SBP_ASSISTANT_PROFILE_CODE = "55";
    public static final String SR_ASSISTANT_PROFILE_CODE = "1024";
    public static final String OCCURENCES = "occurences";
    public static final DateTimeFormatter DDMMYYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static final List<String> ALLOWED_MCO_CODES = List.of("APP_01", "APP_05", "RECO_01", "RECO_109", "SMS_01", "SMS_03");

    public static final String BLUE_PHASE_LABEL = "Bleue";
    public static final String ORANGE_PHASE_LABEL = "Orange";
    public static final String VIOLET_PHASE_LABEL = "Violette";
}
