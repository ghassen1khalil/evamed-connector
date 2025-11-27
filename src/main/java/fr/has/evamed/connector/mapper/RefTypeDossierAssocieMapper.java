package fr.has.evamed.connector.mapper;

import fr.has.evamed.connector.domain.RefTypeDossierAssocie;
import fr.has.evamed.connector.dto.RefTypeDossierAssocieDto;

public class RefTypeDossierAssocieMapper {

    public RefTypeDossierAssocieDto toDto(RefTypeDossierAssocie entity) {
        if (entity == null) {
            return null;
        }
        RefTypeDossierAssocieDto dto = new RefTypeDossierAssocieDto();
        dto.setTdaCode(entity.getTdaCode());
        dto.setTdaLibelle(entity.getTdaLibelle());
        dto.setTdaDateSupLog(entity.getTdaDateSupLog());
        return dto;
    }

    public RefTypeDossierAssocie toEntity(RefTypeDossierAssocieDto dto) {
        if (dto == null) {
            return null;
        }
        RefTypeDossierAssocie entity = new RefTypeDossierAssocie();
        entity.setTdaCode(dto.getTdaCode());
        entity.setTdaLibelle(dto.getTdaLibelle());
        entity.setTdaDateSupLog(dto.getTdaDateSupLog());
        return entity;
    }
}
