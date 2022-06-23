package co.shareleaf;

import co.shareleaf.controller.ControllerConfiguration;
import co.shareleaf.data.DataConfiguration;
import co.shareleaf.props.PropConfiguration;
import co.shareleaf.service.ServiceConfiguration;
import co.shareleaf.utils.mapper.OffsetDateTimeDeserializer;
import co.shareleaf.utils.mapper.OffsetDateTimeSerializer;
import co.shareleaf.utils.migration.FlywayMigration;
import co.shareleaf.utils.migration.FlywayMigrationConfiguration;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.time.OffsetDateTime;

/**
 * @author Bizuwork Melesse
 * created on 2/13/21
 */
@Slf4j
@Configuration
@Import({
        ControllerConfiguration.class,
        DataConfiguration.class,
        PropConfiguration.class,
        ServiceConfiguration.class,
        FlywayMigrationConfiguration.class
})
@RequiredArgsConstructor
public class RootConfiguration {
    private final FlywayMigration flywayMigration;

    @PostConstruct
    public void onApplicationStart() {
        flywayMigration.migrate(false)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(OffsetDateTime.class, new OffsetDateTimeSerializer());
        javaTimeModule.addDeserializer(OffsetDateTime.class, new OffsetDateTimeDeserializer());
        mapper.registerModule(javaTimeModule);
        mapper.registerModule(new JsonNullableModule());
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }
}
