package fr.has.evamed.connector.controller;

import fr.has.evamed.connector.domain.UserTypeDto;
import fr.has.evamed.connector.rest.api.GlobalIndicatorsApi;
import fr.has.evamed.connector.service.GlobalIndicatorsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
public class GlobalIndicatorsController implements GlobalIndicatorsApi {

    private final GlobalIndicatorsService globalIndicatorsService;

    public GlobalIndicatorsController(GlobalIndicatorsService globalIndicatorsService) {
        this.globalIndicatorsService = globalIndicatorsService;
    }

    @Override
    public ResponseEntity<Map<String, Integer>> getAverageTimePerTypology(UserTypeDto userType) {
        log.info("Global Indicators Api - Getting average time per typology with userType={}", userType);
        return ResponseEntity.ok(this.globalIndicatorsService.getGlobalIndicators());
    }

    @Override
    public ResponseEntity<Map<String, Map<String, Integer>>> getProjectTypologyPerDomain(UserTypeDto userType) {
        log.info("Global Indicators Api - Getting project typology per domain with userType={}", userType);
        return GlobalIndicatorsApi.super.getProjectTypologyPerDomain(userType);
    }

    @Override
    public ResponseEntity<Map<String, Integer>> getProjectsByPhase(UserTypeDto userType) {
        log.info("Global Indicators Api - Getting projects by phase with userType={}", userType);
        return GlobalIndicatorsApi.super.getProjectsByPhase(userType);
    }

    @Override
    public ResponseEntity<Map<String, Integer>> getProjectsByTypology(UserTypeDto userType) {
        log.info("Global Indicators Api - Getting projects by typology with userType={}", userType);
        return GlobalIndicatorsApi.super.getProjectsByTypology(userType);
    }
}
