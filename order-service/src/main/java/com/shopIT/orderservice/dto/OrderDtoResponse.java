package com.shopIT.orderservice.dto;

import com.shopIT.orderservice.entity.OrderLineItemsEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDtoResponse {
    private Integer id;
    private String orderNumber;
    private List<OrderLineItemsDtoResponse> orderLineItemsDtoResponseList;
}