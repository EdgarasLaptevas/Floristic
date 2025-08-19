package lt.Edgaras.floristic_backend.service;

import lombok.RequiredArgsConstructor;
import lt.Edgaras.floristic_backend.dto.user.UserMapper;
import lt.Edgaras.floristic_backend.dto.user.UserRequestDTO;
import lt.Edgaras.floristic_backend.dto.user.UserResponseDTO;
import lt.Edgaras.floristic_backend.model.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final UserService userService;

    public UserResponseDTO registerUser(UserRequestDTO dto) {
        User user = userMapper.toEntity(dto);
        userService.saveUser(user);
        return userMapper.toResponseDTO(user);
    }
}
