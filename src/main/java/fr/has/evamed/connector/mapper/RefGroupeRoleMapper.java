package fr.has.evamed.connector.mapper;

import fr.has.evamed.connector.domain.RefGroupeRole;
import fr.has.evamed.connector.dto.RefGroupeRoleDto;

public class RefGroupeRoleMapper {

    public RefGroupeRoleDto toDto(RefGroupeRole entity) {
        if (entity == null) {
            return null;
        }
        RefGroupeRoleDto dto = new RefGroupeRoleDto();
        dto.setGrrCode(entity.getGrrCode());
        dto.setGrrLibelle(entity.getGrrLibelle());
        dto.setGrrDateSupLog(entity.getGrrDateSupLog());
        return dto;
    }

    public RefGroupeRole toEntity(RefGroupeRoleDto dto) {
        if (dto == null) {
            return null;
        }
        RefGroupeRole entity = new RefGroupeRole();
        entity.setGrrCode(dto.getGrrCode());
        entity.setGrrLibelle(dto.getGrrLibelle());
        entity.setGrrDateSupLog(dto.getGrrDateSupLog());
        return entity;
    }
}
