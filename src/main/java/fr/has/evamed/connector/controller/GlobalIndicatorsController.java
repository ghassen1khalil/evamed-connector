package fr.has.evamed.connector.controller;

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
    public ResponseEntity<Map<String, Integer>> getAverageTimePerTyplogy() {
        log.info("Global Indicators Api - Getting average time per typology");
        return ResponseEntity.ok(this.globalIndicatorsService.getGlobalIndicators());
    }

    @Override
    public ResponseEntity<Map<String, Map<String, Integer>>> getProjectTypologyPerDomain() {
        log.info("Global Indicators Api - Getting project typology per domain");
        return GlobalIndicatorsApi.super.getProjectTypologyPerDomain();
    }

    @Override
    public ResponseEntity<Map<String, Integer>> getProjectsByPhase() {
        log.info("Global Indicators Api - Getting projects by phase");
        return GlobalIndicatorsApi.super.getProjectsByPhase();
    }

    @Override
    public ResponseEntity<Map<String, Integer>> getProjectsByTyplogy() {
        log.info("Global Indicators Api - Getting projects by typology");
        return GlobalIndicatorsApi.super.getProjectsByTyplogy();
    }
}
