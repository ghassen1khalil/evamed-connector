package fr.has.evamed.connector.service;

import fr.has.evamed.connector.domain.TypologyPerDomainGlobalIndicatorResponseDto;
import fr.has.evamed.connector.domain.UserTypeDto;
import fr.has.evamed.connector.repository.GlobalIndicatorsRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GlobalIndicatorsService {

    @NonNull private final GlobalIndicatorsRepository globalIndicatorsRepository;

    public Map<String, Integer> getProjectsByTypology(UserTypeDto userType) {
        return this.globalIndicatorsRepository.getProjectsByTypology(userType);
    }

    public Map<String, Integer> getProjectsByPhase(UserTypeDto userType) {
        return this.globalIndicatorsRepository.getProjectsByPhase(userType);
    }

    public Map<String, Integer> getAverageTimePerTypology(UserTypeDto userType) {
        return this.globalIndicatorsRepository.getAverageTimePerTypology(userType);
    }

    public List<TypologyPerDomainGlobalIndicatorResponseDto> getProjectTypologyPerDomain(UserTypeDto userType) {
        return this.globalIndicatorsRepository.getProjectTypologyPerDomain(userType);
    }

    public String getWorkingGroupForCurrentYear(UserTypeDto userType) {
        return this.globalIndicatorsRepository.getWorkingGroupForCurrentYear(userType);
    }
}
