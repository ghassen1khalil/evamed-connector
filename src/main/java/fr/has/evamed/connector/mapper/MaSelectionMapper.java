package fr.has.evamed.connector.mapper;

import fr.has.evamed.connector.domain.MaSelection;
import fr.has.evamed.connector.domain.Utilisateur;
import fr.has.evamed.connector.dto.MaSelectionDto;

public class MaSelectionMapper {

    public MaSelectionDto toDto(MaSelection entity) {
        if (entity == null) {
            return null;
        }
        MaSelectionDto dto = new MaSelectionDto();
        dto.setMseId(entity.getMseId());
        dto.setMseLibelle(entity.getMseLibelle());
        dto.setUtId(entity.getUtilisateur() != null ? entity.getUtilisateur().getUtId() : null);
        return dto;
    }

    public MaSelection toEntity(MaSelectionDto dto) {
        if (dto == null) {
            return null;
        }
        MaSelection entity = new MaSelection();
        entity.setMseId(dto.getMseId());
        entity.setMseLibelle(dto.getMseLibelle());
        if (dto.getUtId() != null) {
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setUtId(dto.getUtId());
            entity.setUtilisateur(utilisateur);
        }
        return entity;
    }
}
