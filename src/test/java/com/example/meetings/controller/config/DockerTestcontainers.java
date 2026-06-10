package com.example.meetings.controller.config;

import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Shared Docker containers for integration tests (started once per JVM).
 */
public final class DockerTestcontainers {

    public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:14-alpine")
            .withDatabaseName("meetings")
            .withUsername("test")
            .withPassword("test");

    private static volatile boolean started;

    private DockerTestcontainers() {
    }

    public static void ensureStarted() {
        if (started) {
            return;
        }
        synchronized (DockerTestcontainers.class) {
            if (started) {
                return;
            }
            POSTGRES.start();
            started = true;
        }
    }
}
