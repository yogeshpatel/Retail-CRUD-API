package com.example.retail.config;

import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * Wraps the H2 DataSource with a datasource-proxy that:
 *  1. Logs every SQL statement via SLF4J (DEBUG level)
 *  2. Injects a configurable artificial delay after each query
 *     to simulate real-database network/disk latency.
 *
 * Toggle via application.yml:
 *   app.db.simulate-latency: true/false
 *   app.db.min-delay-ms:     20
 *   app.db.max-delay-ms:     80
 */
@Configuration
@EnableConfigurationProperties(DataSourceProperties.class)
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${app.db.simulate-latency:true}")
    private boolean simulateLatency;

    @Value("${app.db.min-delay-ms:20}")
    private long minDelayMs;

    @Value("${app.db.max-delay-ms:80}")
    private long maxDelayMs;

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        // Build the real H2 datasource from spring.datasource.* properties
        DataSource h2 = properties.initializeDataSourceBuilder().build();

        if (!simulateLatency) {
            log.info("DB latency simulation DISABLED — using raw H2 datasource");
            return h2;
        }

        log.info("DB latency simulation ENABLED — random delay {}–{} ms per query",
                minDelayMs, maxDelayMs);

        return ProxyDataSourceBuilder
                .create(h2)
                .name("retail-h2-proxy")
                // Log all SQL at DEBUG level (set logging.level.retail-h2-proxy=DEBUG to see them)
                .logQueryBySlf4j(SLF4JLogLevel.DEBUG, "retail-h2-proxy")
                // Inject artificial latency after every query execution
                .afterQuery((execInfo, queryInfoList) -> {
                    long range = maxDelayMs - minDelayMs;
                    long delay = minDelayMs + (range > 0 ? (long) (Math.random() * range) : 0);
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                })
                .build();
    }
}
