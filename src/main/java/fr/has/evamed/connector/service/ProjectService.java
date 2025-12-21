package fr.has.evamed.connector.service;

import fr.has.evamed.connector.domain.*;
import fr.has.evamed.connector.model.ProjectFilters;
import fr.has.evamed.connector.model.UserProfile;
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



    public PaginatedProjectsByUsersResponseDto getProjectsByUsers(UserProfile userProfile, ProjectFilters filters) {
        try {
            int off = filters.getOffset() != null ? filters.getOffset() : 0;
            int lim = filters.getLimit() != null ? filters.getLimit() : 20;
            Map<String, List<ProjectDto>> grouped = new HashMap<>();
            if (UserProfile.MANAGER.equals(userProfile)) {
                grouped = groupProjectsByManagers(filters);
            } else if (UserProfile.ASSISTANT.equals(userProfile)){
                grouped = groupProjectsByAssistants(filters);
            } else {
                log.error("Unsupported user profile: {}", userProfile);
                throw new IllegalArgumentException("Unsupported user profile: " + userProfile);
            }
            int total = projectRepository.countProjects(filters);
            return new PaginatedProjectsByUsersResponseDto(grouped, lim, off, total);
        } catch (Exception e) { //TODO Better exception handling
            log.error(e.getMessage());
        }
        return null;
    }

    private Map<String, List<ProjectDto>> groupProjectsByAssistants(ProjectFilters filters) {
        Map<String, List<ProjectDto>> projectsByAssistants = new HashMap<>();
        try {
            int off = filters.getOffset() != null ? filters.getOffset() : 0;
            int lim = filters.getLimit() != null ? filters.getLimit() : 20;

            if (CollectionUtils.isEmpty(filters.getManagementAssistantsIds())) {
                // Si l'utilisateur n'a pas sélectionné d'assistants de gestion :
                // 1 : On cherche la liste des assistants correspondants aux filtres
                List<ManagementAssistantDto> managementAssistants = projectRepository.getProjectManagementAssistantsByFilter(filters);
                // 2 : On cherche les projets pour les assistants retournés en passant le résultat da la 1ère requête
                if (CollectionUtils.isNotEmpty(managementAssistants)) {
                    filters.setManagementAssistantsIds(managementAssistants.stream().map(ManagementAssistantDto::getId).toList());
                }
                List<ProjectDto> projectsForAssistant = projectRepository.getProjects(off, lim, filters);

                // Regrouper les projets par "Nom Prénom" de l'assistant en évitant les doublons
                Map<String, Set<String>> projectIdsByAssistant = new HashMap<>();
                for (ProjectDto project : projectsForAssistant) {
                    if (project == null || CollectionUtils.isEmpty(project.getManagementAssistant())) {
                        continue;
                    }

                    for (ManagementAssistantDto ma : project.getManagementAssistant()) {
                        if (ma == null) continue;
                        String key = (ma.getLastName() != null ? ma.getLastName() : "") + " " +
                                (ma.getFirstName() != null ? ma.getFirstName() : "").trim();

                        // Initialiser les structures si besoin
                        projectsByAssistants.computeIfAbsent(key, k -> new ArrayList<>());
                        projectIdsByAssistant.computeIfAbsent(key, k -> new HashSet<>());

                        // Éviter les doublons en se basant sur l'ID du projet
                        String projectId = project.getId();
                        if (projectId == null || projectIdsByAssistant.get(key).add(projectId)) {
                            List<ProjectDto> list = projectsByAssistants.get(key);
                            if (projectId != null || !list.contains(project)) {
                                list.add(project);
                            }
                        }
                    }
                }
            } else {
                // Si l'utilisateur a choisi des assistants :
                // 1 : On cherche la liste des assistants correspondant aux filtres
                List<ManagementAssistantDto> managementAssistants = projectRepository.getProjectManagementAssistantsByFilter(filters);

                // 2 : On fait le croisement entre la liste d'assistants retournés par la première requête
                //     et le filtre des assistants sélectionnés par l'utilisateur
                List<String> selectedAssistantIds = filters.getManagementAssistantsIds();
                Set<String> selectedIdsSet = selectedAssistantIds != null ? new HashSet<>(selectedAssistantIds) : new HashSet<>();
                List<String> intersectedIds = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(managementAssistants)) {
                    for (ManagementAssistantDto ma : managementAssistants) {
                        if (ma != null && ma.getId() != null && selectedIdsSet.contains(ma.getId())) {
                            intersectedIds.add(ma.getId());
                        }
                    }
                }

                // 3 : On cherche les projets en passant comme filtre d'assistants le résultat du croisement
                filters.setManagementAssistantsIds(intersectedIds);
                List<ProjectDto> projectsForAssistant = projectRepository.getProjects(off, lim, filters);

                // Regrouper les projets par "Nom Prénom" de l'assistant en évitant les doublons
                Map<String, Set<String>> projectIdsByAssistant = new HashMap<>();
                for (ProjectDto project : projectsForAssistant) {
                    if (project == null || CollectionUtils.isEmpty(project.getManagementAssistant())) {
                        continue;
                    }

                    for (ManagementAssistantDto ma : project.getManagementAssistant()) {
                        if (ma == null) continue;
                        String key = (ma.getLastName() != null ? ma.getLastName() : "") + " " +
                                (ma.getFirstName() != null ? ma.getFirstName() : "").trim();

                        // Initialiser les structures si besoin
                        projectsByAssistants.computeIfAbsent(key, k -> new ArrayList<>());
                        projectIdsByAssistant.computeIfAbsent(key, k -> new HashSet<>());

                        // Éviter les doublons en se basant sur l'ID du projet
                        String projectId = project.getId();
                        if (projectId == null || projectIdsByAssistant.get(key).add(projectId)) {
                            List<ProjectDto> list = projectsByAssistants.get(key);
                            if (projectId != null || !list.contains(project)) {
                                list.add(project);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return projectsByAssistants;
    }

    private Map<String, List<ProjectDto>> groupProjectsByManagers(ProjectFilters filters) {
        Map<String, List<ProjectDto>> projectsByManagers = new HashMap<>();
        try {
            int off = filters.getOffset() != null ? filters.getOffset() : 0;
            int lim = filters.getLimit() != null ? filters.getLimit() : 20;
            if (CollectionUtils.isEmpty(filters.getProjectManagersIds())) {
                // Si l'utilisateur n'a pas sélectionné des CPs :
                // 1 : On cherche la liste des CPs correspondant aux filtres
                // 2 : On cherche les projets pour les CPs retournés en passant le résultat da la 1ère requête
                List<ProjectManagerDto> projectManagers = projectRepository.getProjectsManagersByFilter(filters);
                filters.setProjectManagersIds(projectManagers.stream().map(ProjectManagerDto::getId).toList());
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
                List<ProjectManagerDto> projectManagers = projectRepository.getProjectsManagersByFilter(filters);

                // 2 : On fait le croisement entre la liste de CPs retournés par la première requête
                //     et le filtre des CPs sélectionnés par l'utilisateur
                List<String> selectedManagerIds = filters.getProjectManagersIds();
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
                filters.setProjectManagersIds(intersectedIds);
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
