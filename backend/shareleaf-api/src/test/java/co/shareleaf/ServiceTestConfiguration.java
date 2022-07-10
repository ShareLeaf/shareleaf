package co.shareleaf;

import co.shareleaf.data.DataConfiguration;
import co.shareleaf.props.PropConfiguration;
import co.shareleaf.service.ServiceConfiguration;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootConfiguration
@EnableAutoConfiguration
@Import({
        PropConfiguration.class,
        ServiceConfiguration.class,
        DataConfiguration.class
})
public class ServiceTestConfiguration {

}
