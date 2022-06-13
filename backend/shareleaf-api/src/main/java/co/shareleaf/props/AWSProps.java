package co.shareleaf.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author Bizuwork Melesse
 * created on 2/13/21
 */
@Primary
@Data
@Configuration
@ConfigurationProperties(prefix = "aws")
public class AWSProps {
    private String accessKey;
    private String secretKey;
    private String region;
    private String bucket;
    private String cvPrefix;
}