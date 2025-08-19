package lt.Edgaras.floristic_backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

@Configuration
public class AwsConfig {

    @Bean
    @Primary
    SecretsManagerClient secretsManagerClient() {
        return SecretsManagerClient.create();
    }
}
