package com.shopIT.orderservice.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderLineItemsDtoResponse {
    private Integer id;

    private String skuCode;

    private Integer price;

    private Integer quantity;
}
