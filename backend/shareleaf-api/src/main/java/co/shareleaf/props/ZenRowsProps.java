package co.shareleaf.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author Bizuwork Melesse
 * created on 01/03/2023
 */
@Primary
@Getter @Setter
@Configuration
@ConfigurationProperties(prefix = "zen-rows")
public class ZenRowsProps {
    private String host;
    private String path;
    private String apikey;
}