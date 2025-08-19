package lt.Edgaras.floristic_backend.controller.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lt.Edgaras.floristic_backend.controller.BaseController;
import lt.Edgaras.floristic_backend.dto.ApiResponse;
import lt.Edgaras.floristic_backend.dto.auth.LoginRequestDTO;
import lt.Edgaras.floristic_backend.exception.ApiException;
import lt.Edgaras.floristic_backend.model.User;
import lt.Edgaras.floristic_backend.repository.UserRepository;
import lt.Edgaras.floristic_backend.service.TokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class TokenController extends BaseController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/token")
    public ResponseEntity<ApiResponse<String>> createToken(@Valid @RequestBody LoginRequestDTO dto) {

        User user = userRepository.findByEmail(dto.email()).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new ApiException("Invalid password", HttpStatus.UNAUTHORIZED);
        }

        long accessTokenExpiry = 900L;
        long refreshTokenExpiry = 86400L;

        String accessToken = tokenService.generateAccessToken(user, accessTokenExpiry);
        String refreshToken = tokenService.generateRefreshToken(user, refreshTokenExpiry);
        ResponseCookie refreshCookie = tokenService.createCookie(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new ApiResponse<>(accessToken, "Login successful", true));
    }

    @PostMapping("token/refresh")
    public ResponseEntity<ApiResponse<String>> refreshToken(@CookieValue(name = "refresh_token", required = false) String refreshToken) {
        if (refreshToken == null) {
            throw new ApiException("Missing refresh token", HttpStatus.UNAUTHORIZED);
        }

        if (!tokenService.validateToken(refreshToken, "refresh")) {
            throw new ApiException("Token is not valid", HttpStatus.UNAUTHORIZED);
        }

        String subject = tokenService.getSubject(refreshToken);
        User user = userRepository.findByEmail(subject).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        long accessTokenExpiry = 5400L;
        long refreshTokenExpiry = 86400L;

        String newAccessToken = tokenService.generateAccessToken(user, accessTokenExpiry);
        String newRefreshToken = tokenService.generateRefreshToken(user, refreshTokenExpiry);
        ResponseCookie newRefreshCookie = tokenService.createCookie(newRefreshToken);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, newRefreshCookie.toString())
                .body(new ApiResponse<>(newAccessToken, "Access token rotated successfully", true));
    }
}
