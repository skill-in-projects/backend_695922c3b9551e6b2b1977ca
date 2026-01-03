package com.backend.Config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");
        
        if (databaseUrl == null || databaseUrl.isEmpty()) {
            throw new IllegalStateException("DATABASE_URL environment variable is not set");
        }

        try {
            // Parse PostgreSQL URL format: postgresql://user:password@host:port/database
            URI dbUri = new URI(databaseUrl);
            
            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":").length > 1 ? dbUri.getUserInfo().split(":")[1] : "";
            username = URLDecoder.decode(username, StandardCharsets.UTF_8);
            password = URLDecoder.decode(password, StandardCharsets.UTF_8);
            
            String host = dbUri.getHost();
            int port = dbUri.getPort() > 0 ? dbUri.getPort() : 5432;
            String database = dbUri.getPath().replaceFirst("/", "");
            
            // Build JDBC URL
            String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
            
            // Add query parameters if present (e.g., sslmode)
            if (dbUri.getQuery() != null && !dbUri.getQuery().isEmpty()) {
                jdbcUrl += "?" + dbUri.getQuery();
            }
            
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(username);
            config.setPassword(password);
            config.setMaximumPoolSize(10);
            
            return new HikariDataSource(config);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse DATABASE_URL: " + e.getMessage(), e);
        }
    }
}
