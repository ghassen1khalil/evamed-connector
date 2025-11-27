package fr.has.evamed.connector.mapper;

import fr.has.evamed.connector.domain.RefProfil;
import fr.has.evamed.connector.domain.RefTypeService;
import fr.has.evamed.connector.domain.Utilisateur;
import fr.has.evamed.connector.dto.UtilisateurDto;

public class UtilisateurMapper {

    public UtilisateurDto toDto(Utilisateur entity) {
        if (entity == null) {
            return null;
        }
        UtilisateurDto dto = new UtilisateurDto();
        dto.setUtId(entity.getUtId());
        dto.setPrfCode(entity.getProfil() != null ? entity.getProfil().getPrfCode() : null);
        dto.setSrvCode(entity.getTypeService() != null ? entity.getTypeService().getSrvCode() : null);
        dto.setUtNom(entity.getUtNom());
        dto.setUtPrenom(entity.getUtPrenom());
        dto.setUtLogin(entity.getUtLogin());
        dto.setUtTelephone(entity.getUtTelephone());
        dto.setUtFax(entity.getUtFax());
        dto.setUtEmail(entity.getUtEmail());
        dto.setUtArchive(entity.getUtArchive());
        dto.setUtDateModif(entity.getUtDateModif());
        dto.setUtDenomNorm(entity.getUtDenomNorm());
        return dto;
    }

    public Utilisateur toEntity(UtilisateurDto dto) {
        if (dto == null) {
            return null;
        }
        Utilisateur entity = new Utilisateur();
        entity.setUtId(dto.getUtId());
        entity.setUtNom(dto.getUtNom());
        entity.setUtPrenom(dto.getUtPrenom());
        entity.setUtLogin(dto.getUtLogin());
        entity.setUtTelephone(dto.getUtTelephone());
        entity.setUtFax(dto.getUtFax());
        entity.setUtEmail(dto.getUtEmail());
        entity.setUtArchive(dto.getUtArchive());
        entity.setUtDateModif(dto.getUtDateModif());
        entity.setUtDenomNorm(dto.getUtDenomNorm());
        if (dto.getPrfCode() != null) {
            RefProfil profil = new RefProfil();
            profil.setPrfCode(dto.getPrfCode());
            entity.setProfil(profil);
        }
        if (dto.getSrvCode() != null) {
            RefTypeService typeService = new RefTypeService();
            typeService.setSrvCode(dto.getSrvCode());
            entity.setTypeService(typeService);
        }
        return entity;
    }
}
