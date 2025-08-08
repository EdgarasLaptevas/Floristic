package lt.Edgaras.floristic_backend.controller.auth;

import jakarta.validation.Valid;
import lt.Edgaras.floristic_backend.controller.BaseController;
import lt.Edgaras.floristic_backend.dto.ApiResponse;
import lt.Edgaras.floristic_backend.dto.LoginRequestTDO;
import lt.Edgaras.floristic_backend.model.User;
import lt.Edgaras.floristic_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/auth")
public class TokenController extends BaseController {

    private final UserRepository userRepository;

    @Autowired
    public TokenController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/token")
    public ResponseEntity<ApiResponse<String>> createToken(@Valid @RequestBody LoginRequestTDO tdo) {
        Instant now = Instant.now();
        long expiry = 4800L;

        User user = userRepository.findByEmail(tdo.email()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
