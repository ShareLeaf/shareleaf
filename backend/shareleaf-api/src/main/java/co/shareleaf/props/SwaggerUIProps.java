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
@ConfigurationProperties(prefix = "swagger")
public class SwaggerUIProps {
    private String title;
    private String description;
    private String contact;
    private String email;
    private String version;
    private String url;
    private String apiTos;
    private Boolean enabled;
}