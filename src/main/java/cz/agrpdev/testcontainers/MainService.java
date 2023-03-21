package cz.agrpdev.testcontainers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;


@Service
public class MainService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MainService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getDatabaseUrl() throws Exception {
        return jdbcTemplate.getDataSource().getConnection().getMetaData().getURL();
    }

}
