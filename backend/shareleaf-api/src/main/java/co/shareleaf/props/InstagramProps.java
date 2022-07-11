package co.shareleaf.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Base64;

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
        return decode().split(":")[0];
    }

    public String getPassword() {
        return decode().split(":")[1];
    }

    private String decode() {
        return new String(Base64.getDecoder().decode(pair));
    }
}