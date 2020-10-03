package com.learncamel.routes;

import com.learncamel.CamelApplication;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.Assert.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RunWith(CamelSpringBootRunner.class)
@ActiveProfiles("mock")
@SpringBootTest(classes = {CamelApplication.class},
        properties = {"camel.springboot.java-routes-include-pattern=**/HealthCheckRoute"})

public class HealthCheckMockTest {

    @Autowired
    private Environment environment;

    @Autowired
    private ProducerTemplate producerTemplate;

    @Test
    public void healthRouteTest() {
        String input = " {\"status\":\"DOWN\",\"camel\":{\"status\":\"UP\",\"name\":\"camel-1\",\"version\":\"2.20.1\"," +
                "\"contextStatus\":\"Started\"},\"camel-health-checks\":{\"status\":\"UP\",\"route:healthRoute\":\"UP\"," +
                "\"route:mainRoute\":\"UP\"},\"mail\":{\"status\":\"UP\",\"location\":\"smtp.gmail.com:587\"}," +
                "\"diskSpace\":{\"status\":\"UP\",\"total\":499071844352,\"free\":192566607872,\"threshold\":10485760}," +
                "\"db\":{\"status\":\"DOWN\",\"error\":\"org.springframework.jdbc.CannotGetJdbcConnectionException: " +
                "Could not get JDBC Connection; nested exception is org.postgresql.util.PSQLException: " +
                "Connection to localhost:54321 refused. Check that the hostname and port are correct and that the " +
                "postmaster is accepting TCP/IP connections.\"}}";
        String response = (String) producerTemplate.requestBodyAndHeader(environment.getProperty("healthEndPoint"),
                input, "env", environment.getProperty("spring.profiles"));
        String expectedMessage = "status component in the route is Down\ndb component in the route is Down\n";
        assertEquals(expectedMessage, response);
    }
}