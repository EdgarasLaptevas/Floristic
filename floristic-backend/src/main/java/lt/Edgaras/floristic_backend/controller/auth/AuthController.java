package lt.Edgaras.floristic_backend.controller.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lt.Edgaras.floristic_backend.controller.BaseController;
import lt.Edgaras.floristic_backend.dto.ApiResponse;
import lt.Edgaras.floristic_backend.dto.user.UserRequestDTO;
import lt.Edgaras.floristic_backend.service.AuthService;
import lt.Edgaras.floristic_backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRequestDTO dto) {
        if (userService.findUserByUsername(dto.email()).getEmail().equals(dto.email())) {
            return conflict("Username already exists");
        }

        return ok(authService.registerUser(dto), "User register successfully");
    }
}
