package fr.has.evamed.connector.mapper;

import fr.has.evamed.connector.domain.RefTypeService;
import fr.has.evamed.connector.dto.RefTypeServiceDto;

public class RefTypeServiceMapper {

    public RefTypeServiceDto toDto(RefTypeService entity) {
        if (entity == null) {
            return null;
        }
        RefTypeServiceDto dto = new RefTypeServiceDto();
        dto.setSrvCode(entity.getSrvCode());
        dto.setSrvLibelle(entity.getSrvLibelle());
        dto.setSrvDateSupLog(entity.getSrvDateSupLog());
        return dto;
    }

    public RefTypeService toEntity(RefTypeServiceDto dto) {
        if (dto == null) {
            return null;
        }
        RefTypeService entity = new RefTypeService();
        entity.setSrvCode(dto.getSrvCode());
        entity.setSrvLibelle(dto.getSrvLibelle());
        entity.setSrvDateSupLog(dto.getSrvDateSupLog());
        return entity;
    }
}
