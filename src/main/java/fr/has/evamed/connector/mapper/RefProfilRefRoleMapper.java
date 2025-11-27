package fr.has.evamed.connector.mapper;

import fr.has.evamed.connector.domain.RefProfil;
import fr.has.evamed.connector.domain.RefProfilRefRole;
import fr.has.evamed.connector.domain.RefProfilRefRoleId;
import fr.has.evamed.connector.domain.RefRole;
import fr.has.evamed.connector.dto.RefProfilRefRoleDto;

public class RefProfilRefRoleMapper {

    public RefProfilRefRoleDto toDto(RefProfilRefRole entity) {
        if (entity == null) {
            return null;
        }
        RefProfilRefRoleDto dto = new RefProfilRefRoleDto();
        dto.setPrfCode(entity.getProfil() != null ? entity.getProfil().getPrfCode() : null);
        dto.setRolCode(entity.getRole() != null ? entity.getRole().getRolCode() : null);
        return dto;
    }

    public RefProfilRefRole toEntity(RefProfilRefRoleDto dto) {
        if (dto == null) {
            return null;
        }
        RefProfilRefRole entity = new RefProfilRefRole();
        RefProfilRefRoleId id = new RefProfilRefRoleId();
        id.setPrfCode(dto.getPrfCode());
        id.setRolCode(dto.getRolCode());
        entity.setId(id);
        if (dto.getPrfCode() != null) {
            RefProfil profil = new RefProfil();
            profil.setPrfCode(dto.getPrfCode());
            entity.setProfil(profil);
        }
        if (dto.getRolCode() != null) {
            RefRole role = new RefRole();
            role.setRolCode(dto.getRolCode());
            entity.setRole(role);
        }
        return entity;
    }
}
