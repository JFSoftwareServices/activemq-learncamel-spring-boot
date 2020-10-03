package com.learncamel.processor;

import com.google.gson.Gson;
import com.learncamel.domain.Item;
import com.learncamel.exception.DataException;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
@Slf4j
public class ValidateDataProcessor implements Processor {
    @Override
    public void process(Exchange exchange) {
        String json = exchange.getIn().getBody(String.class);
        Item item = new Gson().fromJson(json, Item.class);
        log.info("Item in ValidateDataProcessor is : " + item);
        if (ObjectUtils.isEmpty(item.getSku())) {
            throw new DataException("Sku is empty for " + item.getItemDescription());
        }
    }
}