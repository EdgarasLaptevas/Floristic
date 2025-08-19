package lt.Edgaras.floristic_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lt.Edgaras.floristic_backend.dto.RsaSecret;
import lt.Edgaras.floristic_backend.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.ResourceNotFoundException;

import java.util.Optional;

@Component
public class RsaSecretLoader {

    private final SecretsManagerClient smc;
    private final String secretId;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RsaSecretLoader(SecretsManagerClient smc, @Value("${JWT_SECRET_ID}") String secretId) {
        this.smc = smc;
        this.secretId = secretId;
    }

    public RsaSecret loadCurrent() {
        return loadByStage("AWSCURRENT").orElseThrow(() -> new ApiException("Current Secret not found", HttpStatus.NOT_FOUND));
    }

    public RsaSecret loadPrevious() {
        return loadByStage("AWSPREVIOUS").orElseThrow(() -> new ApiException("Previous Secret not found", HttpStatus.NOT_FOUND));
    }

    public Optional<RsaSecret> loadByStage(String stage) {
        try {
            GetSecretValueResponse response = smc.getSecretValue(GetSecretValueRequest.builder()
                    .secretId(secretId)
                    .versionStage(stage)
                    .build());

            String versionId = response.versionId();
            JsonNode node = objectMapper.readTree(response.secretString());
            return Optional.of(new RsaSecret(versionId,
                    node.get("jwt.private-key").asText(null),
                    node.get("jwt.public-key").asText(null)));
        } catch (ResourceNotFoundException ex) {
            return Optional.empty();
        } catch (JsonProcessingException ex) {
            throw new ApiException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
