package co.shareleaf.props;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Bizuwork Melesse
 * created on 2/13/21
 *
 */
@Configuration
@ComponentScan
@EnableConfigurationProperties(value = {
        AWSProps.class,
        FlywayProps.class,
        PostgresDataSourceProps.class,
        SourceProps.class,
        SwaggerUIProps.class,
        WebsiteProps.class,
    InstagramProps.class,
    ZenRowsProps.class
})
public class PropConfiguration {
}