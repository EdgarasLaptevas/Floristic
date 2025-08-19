package lt.Edgaras.floristic_backend.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lt.Edgaras.floristic_backend.dto.RsaSecret;
import lt.Edgaras.floristic_backend.service.RsaSecretLoader;
import lt.Edgaras.floristic_backend.util.PemUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Configuration
@RequiredArgsConstructor
public class JwtKeysConfig {

    private final RsaSecretLoader secretLoader;
    private final AtomicReference<JWKSet> encRef = new AtomicReference<>();
    private final AtomicReference<JWKSet> decRef = new AtomicReference<>();

    @PostConstruct //inicijuojame funkcija iskart po aplikacijos paleidimo;
    public void init() {
        refreshKeys();
    }

    public void refreshKeys() {
        RsaSecret current = secretLoader.loadCurrent(); // pasiimame dabartinio ir buvusio secret reiksmes
        RsaSecret previous = secretLoader.loadPrevious();
        JWK encKey = toEncRsaJwk(current);
        encRef.set(new JWKSet(encKey)); // setiname savo gyva nuoroda (atomicReference) kiekvina karta kai yra iskvieciama refreshKeys funkcija su sukurtu pasirasymo raktu(raktais)

        List<JWK> decKeys = new ArrayList<>();
        decKeys.add(toDecRsaJwk(current));
        decKeys.add(toDecRsaJwk(previous));
        decRef.set(new JWKSet(decKeys));
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        JWKSource<SecurityContext> encSource = ((jwkSelector, securityContext) -> jwkSelector.select(encRef.get()));
        return new NimbusJwtEncoder(encSource);
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        JWKSource<SecurityContext> decSource = ((jwkSelector, securityContext) -> jwkSelector.select(decRef.get()));
        var keySelector = new JWSVerificationKeySelector<SecurityContext>(JWSAlgorithm.RS256, decSource);
        var jwtProcessor = new DefaultJWTProcessor<SecurityContext>();
        jwtProcessor.setJWSKeySelector(keySelector);
        return new NimbusJwtDecoder(jwtProcessor);
    }

    //jwt raktu konvertavimas i RSAKey pasirasymo raktus is secrets manager
    private RSAKey toEncRsaJwk(RsaSecret secret) {
        RSAPrivateKey privateKey = PemUtil.toPrivateKey(secret.privateKeyPem());
        RSAPublicKey publicKey = PemUtil.toPublicKey(secret.publicKeyPem());
        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .keyID(secret.versionId())
                .build();
    }

    private RSAKey toDecRsaJwk(RsaSecret secret) {
        RSAPublicKey publicKey = PemUtil.toPublicKey(secret.publicKeyPem());
        return new RSAKey.Builder(publicKey)
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .keyID(secret.versionId())
                .build();
    }
}
