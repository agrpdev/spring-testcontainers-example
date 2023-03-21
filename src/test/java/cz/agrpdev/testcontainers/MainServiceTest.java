package cz.agrpdev.testcontainers;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MainServiceTest extends AbstractAppTest {

    @Autowired
    protected MainService mainService;

    @Test
    public void shouldGetConnectionUrl() throws Exception {
        final String url = mainService.getDatabaseUrl();
        Assert.assertTrue(url.contains("jdbc:postgresql://"));
    }

}