package lt.Edgaras.floristic_backend.dto.user;

import lt.Edgaras.floristic_backend.dto.role.RoleResponseDTO;

import java.util.List;

public record UserResponseDTO(long userId,
                              String username,
                              List<RoleResponseDTO> roleResponseDTOList) {
}
