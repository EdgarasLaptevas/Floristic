package lt.Edgaras.floristic_backend.controller.auth;

import lombok.RequiredArgsConstructor;
import lt.Edgaras.floristic_backend.controller.BaseController;
import lt.Edgaras.floristic_backend.dto.ApiResponse;
import lt.Edgaras.floristic_backend.security.JwtKeysConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class KeysRefreshToken extends BaseController {

    @Value("${KEYS_REFRESH_TOKEN}")
    private String refreshToken;

    private final JwtKeysConfig jwtKeysConfig;

    @PostMapping("/keys/refresh")
    public ResponseEntity<ApiResponse<Void>> refreshKeys(@RequestHeader("X-REFRESH-TOKEN") String token
    ) {
        if (!token.equals(refreshToken)) {
            return badRequest(null, "Failed to refresh keys");
        }

        jwtKeysConfig.refreshKeys();
        return ok("Keys refreshed successfully");
    }
}
