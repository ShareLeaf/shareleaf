package co.shareleaf.data;


import co.shareleaf.data.postgres.PostgresDBDataConfiguration;
import co.shareleaf.props.PropConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Bizuwork Melesse
 * created on 2/13/21
 *
 */
@Configuration
@ComponentScan
@Import({
        PostgresDBDataConfiguration.class,
        PropConfiguration.class
})
public class DataConfiguration {
}