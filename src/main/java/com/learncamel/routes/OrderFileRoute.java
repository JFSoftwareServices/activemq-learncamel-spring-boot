package com.learncamel.routes;

import com.learncamel.processor.BuildSQLProcessor;
import com.learncamel.processor.ValidateDataProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderFileRoute extends RouteBuilder {

    @Autowired
    private ValidateDataProcessor validateProcessor;

    @Autowired
    private BuildSQLProcessor sqlProcessor;

    @Override
    @SuppressWarnings("unchecked")
    public void configure() {
        from("{{inboxFileEndPoint}}?move=processed&moveFailed=error")
                .log("Read Message from inbox ${body}")
                .log("Body is ${body}")
                .process(validateProcessor)
                .process(sqlProcessor)
                .to("{{dataSourceEndPoint}}")
                .to("{{selectNodeEndPoint}}")
                .log("Result from the db table is ${body}");
    }
}