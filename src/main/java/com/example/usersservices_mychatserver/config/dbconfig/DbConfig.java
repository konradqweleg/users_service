package com.example.usersservices_mychatserver.config.dbconfig;


import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.r2dbc.core.DatabaseClient;

@Configuration
@EnableR2dbcRepositories
class DbConfig {

    @Bean
    public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory){
        ConnectionFactoryInitializer initializer =  new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);

        initializer.setDatabasePopulator(
                new ResourceDatabasePopulator(
                        new ClassPathResource("schema.sql")
                )
        );
        return initializer;

    };


    @Bean
    public R2dbcTransactionManager r2dbcTransactionManager(ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }

    @Bean
    public DatabaseClient databaseClient(ConnectionFactory connectionFactory) {
        return DatabaseClient.create(connectionFactory);
    }

}