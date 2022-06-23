package co.shareleaf.data.postgres;


import co.shareleaf.props.PostgresDataSourceProps;
import co.shareleaf.props.SourceProps;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;

import static io.r2dbc.pool.PoolingConnectionFactoryProvider.MAX_SIZE;
import static io.r2dbc.spi.ConnectionFactoryOptions.*;


/**
 * @author Bizuwork Melesse
 * created on 06/12/22
 *
 */
@Configuration
@RequiredArgsConstructor
public class PostgresDBDataConfiguration extends AbstractR2dbcConfiguration {

    private final PostgresDataSourceProps dataSourceProps;

    @Bean
    @Override
    public ConnectionFactory connectionFactory() {
       return new PostgresqlConnectionFactory(
                PostgresqlConnectionConfiguration.builder()
                        .host(dataSourceProps.getHost())
                        .port(dataSourceProps.getPort())
                        .database(dataSourceProps.getDbName())
                        .username(dataSourceProps.getUsername())
                        .password(dataSourceProps.getPassword())
                        .build()
        );
    }
}