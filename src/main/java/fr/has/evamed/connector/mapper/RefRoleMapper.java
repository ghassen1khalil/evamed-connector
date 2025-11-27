package fr.has.evamed.connector.mapper;

import fr.has.evamed.connector.domain.RefGroupeRole;
import fr.has.evamed.connector.domain.RefRole;
import fr.has.evamed.connector.dto.RefRoleDto;

public class RefRoleMapper {

    public RefRoleDto toDto(RefRole entity) {
        if (entity == null) {
            return null;
        }
        RefRoleDto dto = new RefRoleDto();
        dto.setRolCode(entity.getRolCode());
        dto.setRolLibelle(entity.getRolLibelle());
        dto.setRolOrdre(entity.getRolOrdre());
        dto.setRolDateSupLog(entity.getRolDateSupLog());
        dto.setGrrCode(entity.getGroupeRole() != null ? entity.getGroupeRole().getGrrCode() : null);
        return dto;
    }

    public RefRole toEntity(RefRoleDto dto) {
        if (dto == null) {
            return null;
        }
        RefRole entity = new RefRole();
        entity.setRolCode(dto.getRolCode());
        entity.setRolLibelle(dto.getRolLibelle());
        entity.setRolOrdre(dto.getRolOrdre());
        entity.setRolDateSupLog(dto.getRolDateSupLog());
        if (dto.getGrrCode() != null) {
            RefGroupeRole groupeRole = new RefGroupeRole();
            groupeRole.setGrrCode(dto.getGrrCode());
            entity.setGroupeRole(groupeRole);
        }
        return entity;
    }
}
