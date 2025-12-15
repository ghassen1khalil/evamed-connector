package fr.has.evamed.connector.controller;

import fr.has.evamed.connector.domain.TypologyPerDomainGlobalIndicatorResponseDto;
import fr.has.evamed.connector.domain.UserTypeDto;
import fr.has.evamed.connector.rest.api.GlobalIndicatorsApi;
import fr.has.evamed.connector.service.GlobalIndicatorsService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class GlobalIndicatorsController implements GlobalIndicatorsApi {

    @NonNull private final GlobalIndicatorsService globalIndicatorsService;

    @Override
    public ResponseEntity<Map<String, Integer>> getProjectsByTypology(UserTypeDto userType) {
        log.info("Global Indicators Api - Getting projects by typology with userType={}", userType);
        return ResponseEntity.ok(this.globalIndicatorsService.getProjectsByTypology(userType));
    }

    @Override
    public ResponseEntity<Map<String, Integer>> getProjectsByPhase(UserTypeDto userType) {
        log.info("Global Indicators Api - Getting projects by phase with userType={}", userType);
        return ResponseEntity.ok(this.globalIndicatorsService.getProjectsByPhase(userType));
    }

    @Override
    public ResponseEntity<Map<String, Integer>> getAverageTimePerTypology(UserTypeDto userType) {
        log.info("Global Indicators Api - Getting average time per typology with userType={}", userType);
        return ResponseEntity.ok(this.globalIndicatorsService.getAverageTimePerTypology(userType));
    }

    @Override
    public ResponseEntity<List<TypologyPerDomainGlobalIndicatorResponseDto>> getProjectTypologyPerDomain(UserTypeDto userType) {
        log.info("Global Indicators Api - Getting project typology per domain with userType={}", userType);
        return ResponseEntity.ok(this.globalIndicatorsService.getProjectTypologyPerDomain(userType));
    }

    @Override
    public ResponseEntity<String> getWorkingGroupForCurrentYear(UserTypeDto userType) {
        return ResponseEntity.ok(this.globalIndicatorsService.getWorkingGroupForCurrentYear(userType));
    }
}
