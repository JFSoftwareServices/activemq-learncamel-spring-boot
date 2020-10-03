package com.learncamel.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
@AllArgsConstructor
public class Item {
    private String transactionType;
    private String sku;
    private String itemDescription;
    private BigDecimal price;
}