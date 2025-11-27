package fr.has.evamed.connector.mapper;

import fr.has.evamed.connector.domain.MaPageUtilisateur;
import fr.has.evamed.connector.domain.Utilisateur;
import fr.has.evamed.connector.dto.MaPageUtilisateurDto;

public class MaPageUtilisateurMapper {

    public MaPageUtilisateurDto toDto(MaPageUtilisateur entity) {
        if (entity == null) {
            return null;
        }
        MaPageUtilisateurDto dto = new MaPageUtilisateurDto();
        dto.setMpaCode(entity.getMpaCode());
        dto.setUtId(entity.getUtilisateur() != null ? entity.getUtilisateur().getUtId() : null);
        dto.setMpaLibelle(entity.getMpaLibelle());
        dto.setMpaDateSupLog(entity.getMpaDateSupLog());
        dto.setOrdre(entity.getOrdre());
        return dto;
    }

    public MaPageUtilisateur toEntity(MaPageUtilisateurDto dto) {
        if (dto == null) {
            return null;
        }
        MaPageUtilisateur entity = new MaPageUtilisateur();
        entity.setMpaCode(dto.getMpaCode());
        if (dto.getUtId() != null) {
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setUtId(dto.getUtId());
            entity.setUtilisateur(utilisateur);
        }
        entity.setMpaLibelle(dto.getMpaLibelle());
        entity.setMpaDateSupLog(dto.getMpaDateSupLog());
        entity.setOrdre(dto.getOrdre());
        return entity;
    }
}
