package org.ibs.orderservice.dto;

import lombok.Data;

@Data
public class OrderItemRequest {

    private Long productId;

    private Integer quantity;
}