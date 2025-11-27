package fr.has.evamed.connector.mapper;

import fr.has.evamed.connector.domain.RefListeMaPage;
import fr.has.evamed.connector.domain.RefListeMaPageId;
import fr.has.evamed.connector.domain.RefTypeDossierAssocie;
import fr.has.evamed.connector.domain.Utilisateur;
import fr.has.evamed.connector.dto.RefListeMaPageDto;

public class RefListeMaPageMapper {

    public RefListeMaPageDto toDto(RefListeMaPage entity) {
        if (entity == null) {
            return null;
        }
        RefListeMaPageDto dto = new RefListeMaPageDto();
        dto.setImpCode(entity.getId() != null ? entity.getId().getImpCode() : null);
        dto.setUtId(entity.getUtilisateur() != null ? entity.getUtilisateur().getUtId() : null);
        dto.setImpLibelle(entity.getImpLibelle());
        dto.setImpDateSupLog(entity.getImpDateSupLog());
        dto.setTdaCode(entity.getTypeDossierAssocie() != null ? entity.getTypeDossierAssocie().getTdaCode() : null);
        dto.setOrdre(entity.getOrdre());
        return dto;
    }

    public RefListeMaPage toEntity(RefListeMaPageDto dto) {
        if (dto == null) {
            return null;
        }
        RefListeMaPage entity = new RefListeMaPage();
        RefListeMaPageId id = new RefListeMaPageId();
        id.setImpCode(dto.getImpCode());
        id.setUtId(dto.getUtId());
        entity.setId(id);
        if (dto.getUtId() != null) {
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setUtId(dto.getUtId());
            entity.setUtilisateur(utilisateur);
        }
        if (dto.getTdaCode() != null) {
            RefTypeDossierAssocie typeDossierAssocie = new RefTypeDossierAssocie();
            typeDossierAssocie.setTdaCode(dto.getTdaCode());
            entity.setTypeDossierAssocie(typeDossierAssocie);
        }
        entity.setImpLibelle(dto.getImpLibelle());
        entity.setImpDateSupLog(dto.getImpDateSupLog());
        entity.setOrdre(dto.getOrdre());
        return entity;
    }
}
