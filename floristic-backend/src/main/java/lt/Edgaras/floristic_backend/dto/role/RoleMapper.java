package lt.Edgaras.floristic_backend.dto.role;

import lt.Edgaras.floristic_backend.model.Role;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    Role toEntity(RoleRequestDTO dto);

    RoleResponseDTO toResponseDto(Role role);

    List<RoleResponseDTO> toResponseDtoList(List<Role> roles);
}
