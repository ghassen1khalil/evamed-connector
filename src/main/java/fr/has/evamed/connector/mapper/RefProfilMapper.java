package fr.has.evamed.connector.mapper;

import fr.has.evamed.connector.domain.RefProfil;
import fr.has.evamed.connector.dto.RefProfilDto;

public class RefProfilMapper {

    public RefProfilDto toDto(RefProfil entity) {
        if (entity == null) {
            return null;
        }
        RefProfilDto dto = new RefProfilDto();
        dto.setPrfCode(entity.getPrfCode());
        dto.setPrfLibelle(entity.getPrfLibelle());
        dto.setPrfDateSupLog(entity.getPrfDateSupLog());
        return dto;
    }

    public RefProfil toEntity(RefProfilDto dto) {
        if (dto == null) {
            return null;
        }
        RefProfil entity = new RefProfil();
        entity.setPrfCode(dto.getPrfCode());
        entity.setPrfLibelle(dto.getPrfLibelle());
        entity.setPrfDateSupLog(dto.getPrfDateSupLog());
        return entity;
    }
}
