package bg.chess.api.config.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secretKey = "default_flzxsq3c±ysyhl@34WQjt";
    private long validityInMs = Duration.ofHours(24).toMillis();
}
