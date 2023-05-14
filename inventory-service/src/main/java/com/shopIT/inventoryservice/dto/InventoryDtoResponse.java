package com.shopIT.inventoryservice.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data   //This will be the equivalent of @Getters, @Setters, @ToString, @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder   // To implement Builder Design
public class InventoryDtoResponse {

        private Integer id;

        private String skuCode;

        private Integer quantity;
}
