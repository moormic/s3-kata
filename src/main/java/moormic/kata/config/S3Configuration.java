package moormic.kata.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;

@Data
@Component
@Configuration
@ConfigurationProperties("s3")
public class S3Configuration {
    private String accessKey;
    private String secretKey;
    private String region;
}
