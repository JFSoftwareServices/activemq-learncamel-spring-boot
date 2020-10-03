package com.learncamel.processor;

import com.google.gson.Gson;
import com.learncamel.domain.Item;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BuildSQLProcessor implements org.apache.camel.Processor {
    @Override
    public void process(Exchange exchange) {
//        Item item = (Item) exchange.getIn().getBody();
        String json = exchange.getIn().getBody(String.class);
        Item item = new Gson().fromJson(json, Item.class);
        log.info(" Item in BuildSQLProcessor is : " + item);
        String tableName = "ITEMS";
        StringBuilder query = new StringBuilder();

        switch (item.getTransactionType()) {
            case "ADD":
                query.append("INSERT INTO ").append(tableName).append(" (SKU, ITEM_DESCRIPTION,PRICE) VALUES ('");
                query.append(item.getSku()).append("','").append(item.getItemDescription()).append("',").append(item.getPrice()).append(");");

                break;
            case "UPDATE":
                query.append("UPDATE ").append(tableName).append(" SET PRICE =");
                query.append(item.getPrice()).append(" where SKU = '").append(item.getSku()).append("'");

                break;
            case "DELETE":
                query.append("DELETE FROM ").append(tableName).append(" where SKU = '").append(item.getSku()).append("'");
                break;
        }
        log.info("Final Query is : " + query);
        exchange.getIn().setBody(query.toString());
        exchange.getIn().setHeader("skuId", item.getSku());
    }
}