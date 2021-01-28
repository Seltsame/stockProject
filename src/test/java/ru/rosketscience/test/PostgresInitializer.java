package ru.rosketscience.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
public class PostgresInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String POSTGRES_IMAGE = "postgres:11.10";

    private static PostgreSQLContainer<?> postgreSQLContainer;

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        try {
            log.info("Starting PostgreSQL in Docker from " + POSTGRES_IMAGE);
            postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse(POSTGRES_IMAGE))
                    .withUsername("postgres")
                    .withPassword("qwerty");
            postgreSQLContainer.start();
            log.info("PostgreSQL in Docker successfully started from " + POSTGRES_IMAGE);

            applicationContext.addApplicationListener(event -> {
                if (event instanceof ApplicationFailedEvent
                    || event instanceof ContextClosedEvent
                    || event instanceof ContextStoppedEvent)
                {
                    log.debug("PostgreSQL in Docker is being stopped " + POSTGRES_IMAGE);
                    postgreSQLContainer.close();
                    log.info("PostgreSQL in Docker was successfully stopped: " + POSTGRES_IMAGE);
                }
            });
        } catch (Exception e) {
            log.error("Failed to start PostgreSQL in Docker from " + POSTGRES_IMAGE, e);
        }
    }

    static String getJdbcUrl() {
        return postgreSQLContainer.getJdbcUrl();
    }
}
