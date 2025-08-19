package lt.Edgaras.floristic_backend.service;


import lombok.RequiredArgsConstructor;
import lt.Edgaras.floristic_backend.model.User;
import org.springframework.http.ResponseCookie;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;


import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final RsaSecretLoader secretLoader;

    public String generateAccessToken(User user, long expiry) {
        return generateToken(user, expiry, "access");
    }

    public String generateRefreshToken(User user, long expiry) {
        return generateToken(user, expiry, "refresh");
    }

    public String generateToken(User user, long expiry, String type) {

        String kid = secretLoader.loadCurrent().versionId();
        JwsHeader headers = JwsHeader.with(() -> "RS256")
                .header("kid", kid)
                .build();

        Instant now = Instant.now();
        String scope = user.getRoles().stream().map((role) -> role.getName().toUpperCase()).collect(Collectors.joining(" "));

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(user.getEmail())
                .claim("user_id", user.getUserId())
                .claim("scope", scope)
                .claim("type", type)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(headers, jwtClaimsSet)).getTokenValue();
    }

    public boolean validateToken(String token, String expectedType) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            Instant expiry = jwt.getExpiresAt();
            String type = jwt.getClaim("type");

            if (expiry == null || expiry.isBefore(new Date().toInstant())) {
                return false;
            }

            return type != null && type.equals(expectedType);

        } catch (JwtException e) {
            return false;
        }
    }

    public String getSubject(String token) {
        Jwt jwt = jwtDecoder.decode(token);

        return jwt.getSubject();
    }

    public ResponseCookie createCookie(String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("none")
                .path("/auth/token/refresh")
                .maxAge(Duration.ofDays(1))
                .build();
    }
}
