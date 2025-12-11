package fr.has.evamed.connector.controller;

import fr.has.evamed.connector.rest.api.GlobalIndicatorsApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GlobalIndicatorsController implements GlobalIndicatorsApi {

    @Override
    public ResponseEntity<Map<String, Integer>> getAverageTimePerTyplogy() {
        return GlobalIndicatorsApi.super.getAverageTimePerTyplogy();
    }

    @Override
    public ResponseEntity<Map<String, Map<String, Integer>>> getProjectTypologyPerDomain() {
        return GlobalIndicatorsApi.super.getProjectTypologyPerDomain();
    }

    @Override
    public ResponseEntity<Map<String, Integer>> getProjectsByPhase() {
        return GlobalIndicatorsApi.super.getProjectsByPhase();
    }

    @Override
    public ResponseEntity<Map<String, Integer>> getProjectsByTyplogy() {
        return GlobalIndicatorsApi.super.getProjectsByTyplogy();
    }
}
