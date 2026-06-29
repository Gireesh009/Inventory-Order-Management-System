package org.ibs.productservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryResponseDTO {

    private Long productId;
    private String name;
    private Double price;
    private String currency;
    private Integer stock;
    private String status; // IN_STOCK / LOW_STOCK / OUT_OF_STOCK

}
