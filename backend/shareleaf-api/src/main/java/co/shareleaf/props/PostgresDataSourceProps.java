package co.shareleaf.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author Bizuwork Melesse
 * created on 2/13/21
 *
 */
@Primary
@Data
@Configuration
@ConfigurationProperties("spring.datasource.reactive")
public class PostgresDataSourceProps {
    private String driverClassName;
    private String url;
    private String username;
    private String password;
}
