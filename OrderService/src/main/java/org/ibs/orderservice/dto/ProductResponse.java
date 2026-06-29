package org.ibs.orderservice.dto;

import lombok.Data;

@Data
public class ProductResponse {

    private Long id;

    private String name;

    private Double price;
    private String currency;
    private Integer stock;
}