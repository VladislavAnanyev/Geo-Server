package com.example.mywebquizengine.config;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import org.springframework.context.annotation.*;

import javax.sql.DataSource;

@Configuration
@ComponentScan
@Profile("test")
public class TestDbConfig {

    @Bean
    @Primary
    public DataSource inMemoryDS() throws Exception {
        return EmbeddedPostgres.builder()
                .start()
                .getPostgresDatabase();
    }
}