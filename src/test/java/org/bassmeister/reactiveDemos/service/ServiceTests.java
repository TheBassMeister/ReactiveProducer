package org.bassmeister.reactiveDemos.service;

import static java.lang.String.format;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
@ActiveProfiles("test")
public abstract class ServiceTests {

  protected static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:15-alpine");

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    String jdbcUrl = postgres.getJdbcUrl();
    registry.add("spring.liquibase.url", () -> jdbcUrl);
    registry.add("spring.liquibase.user", postgres::getUsername);
    registry.add("spring.liquibase.password", postgres::getPassword);

    registry.add(
        "spring.r2dbc.url",
        () ->
            format(
                "r2dbc:pool:postgresql://%s:%d/%s",
                postgres.getHost(), postgres.getFirstMappedPort(), postgres.getDatabaseName()));
    registry.add("spring.r2dbc.username", postgres::getUsername);
    registry.add("spring.r2dbc.password", postgres::getPassword);
  }

  @BeforeAll
  protected static void startContainer() {
    postgres.start();
  }

  @AfterAll
  protected static void shutdown() {
    postgres.stop();
  }
}
