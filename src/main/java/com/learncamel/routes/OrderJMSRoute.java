package com.learncamel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class OrderJMSRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {

    }

    /*@Autowired
    Environment environment;

    @Qualifier("dataSource")
    @Autowired
    DataSource dataSource;

    @Autowired
    MailProcessor mailProcessor;

    @Autowired
    ValidateDataProcessor validateProcessor;

    @Autowired
    BuildSQLProcessor sqlProcessor;

    @Override
    @SuppressWarnings("unchecked")
    public void configure() {
        Predicate isNotMock = header("env").isNotEqualTo("mock");

        onException(PSQLException.class).log(LoggingLevel.ERROR, "PSQLException in the route ${body}")
                .maximumRedeliveries(3).redeliveryDelay(3000).backOffMultiplier(2).retryAttemptedLogLevel(LoggingLevel.ERROR);

        onException(DataException.class, RuntimeException.class).log(LoggingLevel.ERROR, "DataException in the route ${body}")
                .choice()
                .when(isNotMock)
//                .process(mailProcessor)
                .end()
                .log("Body in Exception Block is ${body}")
                .setBody(constant(body()))
                .to("{{errorQueueRoute}}");

        from("{{fromQueueRoute}}")
                .log("Read Message from ActiveMQ ${body}")
                .unmarshal().json(JsonLibrary.Gson)
                .log("UnMarshaled Message is ${body}")
                .process(validateProcessor)
                .process(sqlProcessor)
                .to("{{toDBRoute}}")
                .to("{{selectNode}}")
                .log("Result from the db table is ${body}");







inputItemQueue: jms:queue:inputItemQueue
        dataSource: jdbc:dataSource
        errorQueue:  jms:queue:errorItemQueue
        selectNode: sql:select * from items where sku = :#skuId?dataSource=#dataSource
        healthTimer: timer:health?period=10s
    }*/
}