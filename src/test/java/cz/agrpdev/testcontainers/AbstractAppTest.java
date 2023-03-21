package cz.agrpdev.testcontainers;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Import(AbstractAppTest.BeanOverrides.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"grpc.port=0"}, classes = {App.class})
@ContextConfiguration(initializers = {AbstractAppTest.Initializer.class})
public abstract class AbstractAppTest {

    private static final PgEnv pgEnv = new PgEnv();

    @LocalServerPort
    protected int port;
    @Autowired
    protected JdbcTemplate template;

    @Before
    public void before() {
        pgEnv.cleanup(template);
    }

    @TestConfiguration
    static class BeanOverrides {

        // place to override beans for tests

    }

    @Configuration
    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "grpc.port=0",
                    "spring.datasource.url=" + pgEnv.getJdbcUrl(),
                    "spring.datasource.username=" + pgEnv.getUsername(),
                    "spring.datasource.password=" + pgEnv.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

}