package com.learncamel.routes;

import com.learncamel.alert.MailProcessor;
import com.learncamel.processor.HealthCheckProcessor;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HealthCheckRoute extends RouteBuilder{
    @Autowired
    private HealthCheckProcessor healthCheckProcessor;

    @Autowired
    private MailProcessor mailProcessor;

    private Predicate isNotDev =  header("env").isNotEqualTo("mock");

    @Override
    public void configure()  {
            from("{{healthEndPoint}}").routeId("healthRoute")
                .choice()
                .when(isNotDev)
                        .pollEnrich("http://localhost:8080/actuator/health")
                    .end()
                .process(healthCheckProcessor)
                .choice()
                    .when(header("error").isEqualTo(true))
                        .choice()
                            .when(isNotDev)
                                .process(mailProcessor)
                            .end()
                .end();
    }
}