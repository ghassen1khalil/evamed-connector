package fr.has.evamed.connector.mapper;

import fr.has.evamed.connector.domain.ListePartageUtilisateur;
import fr.has.evamed.connector.domain.ListePartageUtilisateurId;
import fr.has.evamed.connector.domain.MaSelection;
import fr.has.evamed.connector.domain.Utilisateur;
import fr.has.evamed.connector.dto.ListePartageUtilisateurDto;

public class ListePartageUtilisateurMapper {

    public ListePartageUtilisateurDto toDto(ListePartageUtilisateur entity) {
        if (entity == null) {
            return null;
        }
        ListePartageUtilisateurDto dto = new ListePartageUtilisateurDto();
        dto.setMseId(entity.getSelection() != null ? entity.getSelection().getMseId() : null);
        dto.setUtId(entity.getUtilisateur() != null ? entity.getUtilisateur().getUtId() : null);
        return dto;
    }

    public ListePartageUtilisateur toEntity(ListePartageUtilisateurDto dto) {
        if (dto == null) {
            return null;
        }
        ListePartageUtilisateur entity = new ListePartageUtilisateur();
        ListePartageUtilisateurId id = new ListePartageUtilisateurId();
        id.setMseId(dto.getMseId());
        id.setUtId(dto.getUtId());
        entity.setId(id);
        if (dto.getMseId() != null) {
            MaSelection selection = new MaSelection();
            selection.setMseId(dto.getMseId());
            entity.setSelection(selection);
        }
        if (dto.getUtId() != null) {
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setUtId(dto.getUtId());
            entity.setUtilisateur(utilisateur);
        }
        return entity;
    }
}
