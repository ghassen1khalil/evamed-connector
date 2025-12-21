package fr.has.evamed.connector.service;

import fr.has.evamed.connector.domain.PaginatedProjectResponseDto;
import fr.has.evamed.connector.domain.ProjectDto;
import fr.has.evamed.connector.domain.ProjectManagerDto;
import fr.has.evamed.connector.domain.ManagementAssistantDto;
import fr.has.evamed.connector.domain.UserTypeDto;
import fr.has.evamed.connector.model.ProjectFilters;
import fr.has.evamed.connector.repository.ProjectRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    @NonNull private final ProjectRepository projectRepository;

    public PaginatedProjectResponseDto getProjects(ProjectFilters filters) {
        try {
            int off = filters.getOffset() != null ? filters.getOffset() : 0;
            int lim = filters.getLimit() != null ? filters.getLimit() : 20;
            List<ProjectDto> projects = projectRepository.getProjects(off, lim, filters);
            int total = projectRepository.countProjects(filters);
            return new PaginatedProjectResponseDto(projects, lim, off, total);
        } catch (Exception e) { //TODO Better exception handling
            log.error(e.getMessage());
        }
        return null;
    }

    public List<ProjectManagerDto> getProjectManagers(UserTypeDto userType) {
        return projectRepository.getProjectManagers(userType);
    }

    public List<ManagementAssistantDto> getManagementAssistants(UserTypeDto userType) {
        return projectRepository.getManagementAssistants(userType);
    }

    public List<String> getTypologies(UserTypeDto userType) {
        return projectRepository.getTypologies(userType);
    }

    public Map<String, List<ProjectDto>> groupProjectsByManagers(ProjectFilters filters) {
        Map<String, List<ProjectDto>> projectsByManagers = new HashMap<>();
        try {
            int off = filters.getOffset() != null ? filters.getOffset() : 0;
            int lim = filters.getLimit() != null ? filters.getLimit() : 20;
            if (CollectionUtils.isEmpty(filters.getProjectManagerId())) {
                // Si l'utilisateur n'a pas sélectionné des CPs :
                // 1 : On cherche la liste des CPs correspondant aux filtres
                // 2 : On cherche les projets pour les CPs retournés en passant le résultat da la 1ère requête
                List<ProjectManagerDto> projectManagers = projectRepository.getProjectManagersByFilter(filters);
                filters.setProjectManagerId(projectManagers.stream().map(ProjectManagerDto::getId).toList());
                List<ProjectDto> projectsForManager = projectRepository.getProjects(off, lim, filters);
                // Regrouper les projets par "Nom Prénom" du manager primaire en évitant les doublons
                Map<String, Set<String>> projectIdsByManager = new HashMap<>();
                for (ProjectDto project : projectsForManager) {
                    if (project == null || project.getProjectManagers() == null ||
                            CollectionUtils.isEmpty(project.getProjectManagers().getPrimary())) {
                        continue;
                    }

                    for (ProjectManagerDto pm : project.getProjectManagers().getPrimary()) {
                        if (pm == null) continue;
                        String key = (pm.getLastName() != null ? pm.getLastName() : "") + " " +
                                (pm.getFirstName() != null ? pm.getFirstName() : "").trim();

                        // Initialiser les structures si besoin
                        projectsByManagers.computeIfAbsent(key, k -> new ArrayList<>());
                        projectIdsByManager.computeIfAbsent(key, k -> new HashSet<>());

                        // Éviter les doublons en se basant sur l'ID du projet
                        String projectId = project.getId();
                        if (projectId == null || projectIdsByManager.get(key).add(projectId)) {
                            // Ajoute le projet si id inconnu ou manquant (conservateur)
                            List<ProjectDto> list = projectsByManagers.get(key);
                            // Éviter les doublons éventuels si id null en vérifiant la présence
                            if (projectId != null || !list.contains(project)) {
                                list.add(project);
                            }
                        }
                    }
                }
            } else {
                // Si l'utilisateur a choisi des CPs :
                // 1 : On cherche la liste des CPs correspondant aux filtres
                List<ProjectManagerDto> projectManagers = projectRepository.getProjectManagersByFilter(filters);

                // 2 : On fait le croisement entre la liste de CPs retournés par la première requête
                //     et le filtre des CPs sélectionnés par l'utilisateur
                List<String> selectedManagerIds = filters.getProjectManagerId();
                Set<String> selectedIdsSet = selectedManagerIds != null ? new HashSet<>(selectedManagerIds) : new HashSet<>();
                List<String> intersectedIds = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(projectManagers)) {
                    for (ProjectManagerDto pm : projectManagers) {
                        if (pm != null && pm.getId() != null && selectedIdsSet.contains(pm.getId())) {
                            intersectedIds.add(pm.getId());
                        }
                    }
                }

                // 3 : On cherche les projets en passant comme filtre de CP le résultat du croisement
                filters.setProjectManagerId(intersectedIds);
                List<ProjectDto> projectsForManager = projectRepository.getProjects(off, lim, filters);

                // Regrouper les projets par "Nom Prénom" du manager primaire en évitant les doublons
                Map<String, Set<String>> projectIdsByManager = new HashMap<>();
                for (ProjectDto project : projectsForManager) {
                    if (project == null || project.getProjectManagers() == null ||
                            CollectionUtils.isEmpty(project.getProjectManagers().getPrimary())) {
                        continue;
                    }

                    for (ProjectManagerDto pm : project.getProjectManagers().getPrimary()) {
                        if (pm == null) continue;
                        String key = (pm.getLastName() != null ? pm.getLastName() : "") + " " +
                                (pm.getFirstName() != null ? pm.getFirstName() : "").trim();

                        // Initialiser les structures si besoin
                        projectsByManagers.computeIfAbsent(key, k -> new ArrayList<>());
                        projectIdsByManager.computeIfAbsent(key, k -> new HashSet<>());

                        // Éviter les doublons en se basant sur l'ID du projet
                        String projectId = project.getId();
                        if (projectId == null || projectIdsByManager.get(key).add(projectId)) {
                            // Ajoute le projet si id inconnu ou manquant (conservateur)
                            List<ProjectDto> list = projectsByManagers.get(key);
                            // Éviter les doublons éventuels si id null en vérifiant la présence
                            if (projectId != null || !list.contains(project)) {
                                list.add(project);
                            }
                        }
                    }
                }
            }
        }catch (Exception e) {
            log.error(e.getMessage());
        }
        return projectsByManagers;
    }
}
