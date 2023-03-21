package cz.agrpdev.testcontainers;

import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;

public class PgEnv {

    private static final String TRUNCATE_ALL = """
            CREATE OR REPLACE FUNCTION truncate_tables(username IN VARCHAR) RETURNS void AS $$
            DECLARE
                statements CURSOR FOR
                    SELECT tablename FROM pg_tables
                    WHERE tableowner = username AND schemaname = 'public';
            BEGIN
                FOR stmt IN statements LOOP
                    EXECUTE 'TRUNCATE TABLE ' || quote_ident(stmt.tablename) || ' CASCADE;';
                END LOOP;
            END;
            $$ LANGUAGE plpgsql;
            """;

    private final String jdbcUrl;
    private final String username;
    private final String password;
    public final PostgreSQLContainer pgContainer;

    public PgEnv(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        this.pgContainer = null;
    }

    public PgEnv() {
        pgContainer = new PostgreSQLContainer("postgres:14");
        if (!pgContainer.isRunning()) {
            pgContainer.start();
        }
        this.jdbcUrl = pgContainer.getJdbcUrl();
        this.username = pgContainer.getUsername();
        this.password = pgContainer.getPassword();
        LoggerFactory.getLogger(PgEnv.class).info("PostgreSQL JDBC URL: {}", pgContainer.getJdbcUrl());
    }

    public void cleanup(final JdbcTemplate template) {
        try {
            template.execute(TRUNCATE_ALL);
            template.execute("SELECT truncate_tables('" + getUsername() + "')");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}