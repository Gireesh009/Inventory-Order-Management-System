package org.ibs.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockUpdateRequest {
    private Long productId;
    private Integer quantity;

    public Integer quantity() {
        return quantity;
    }
}
