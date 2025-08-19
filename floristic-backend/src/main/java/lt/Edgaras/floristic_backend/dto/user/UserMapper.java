package lt.Edgaras.floristic_backend.dto.user;

import lt.Edgaras.floristic_backend.dto.role.RoleMapper;
import lt.Edgaras.floristic_backend.dto.role.RoleResponseDTO;
import lt.Edgaras.floristic_backend.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {

    User toEntity(UserRequestDTO dto);

    @Mapping(source = "role", target = "roleResponseDTO")
    UserResponseDTO toResponseDTO(User user);

    List<UserResponseDTO> toResponseDtoList(List<User> users);
}
