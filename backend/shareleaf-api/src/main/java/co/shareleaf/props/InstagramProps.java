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
@ConfigurationProperties(prefix = "instagram")
public class InstagramProps {
    private String pair;

    public String getUsername() {
        return pair.split(":")[0];
    }

    public String getPassword() {
        return pair.split(":")[1];
    }
}