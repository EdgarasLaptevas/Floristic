package lt.Edgaras.floristic_backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class JwtKeyProvider {

    private static final String BEGIN_PRIVATE = "-----BEGIN PRIVATE KEY-----";
    private static final String END_PRIVATE = "-----END PRIVATE KEY-----";
    private static final String BEGIN_PUBLIC = "-----BEGIN PUBLIC KEY-----";
    private static final String END_PUBLIC = "-----END PUBLIC KEY-----";

    @Value("${jwt.private-key}")
    private String privateKeyPem;

    @Value("${jwt.public-key}")
    private String publicKeyPem;

    public RSAPrivateKey getPrivateKey() {
        try {
            String cleaned = privateKeyPem
                    .replace(BEGIN_PRIVATE, "")
                    .replace(END_PRIVATE, "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(cleaned);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load private key from secret", e);
        }
    }

    public RSAPublicKey getPublicKey() {
        try {
            String cleaned = publicKeyPem
                    .replace(BEGIN_PUBLIC, "")
                    .replace(END_PUBLIC, "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(cleaned);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(keySpec);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load public key from secret", e);
        }
    }
}
