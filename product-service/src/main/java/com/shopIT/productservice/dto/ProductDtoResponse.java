package com.shopIT.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data   //This will be the equivalent of @Getters, @Setters, @ToString, @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder   // To implement Builder Design - search this
public class ProductDtoResponse implements Serializable {

    private Integer product_id;

    private String product_name;

    private String description;

    private Integer price;
}
