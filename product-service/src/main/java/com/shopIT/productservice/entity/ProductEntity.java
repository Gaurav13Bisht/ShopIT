package com.shopIT.productservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Data   //This will be the equivalent of @Getters, @Setters, @ToString, @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder   // To implement Builder Design
@Entity  // This is used to map this class to relational DB table. @Document is used for non-relational DB like mongoDB
@SequenceGenerator(name="seq", initialValue=100, allocationSize = 1)   // This is used to generate the ID as required
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    private Integer product_id;

    private String product_name;
    private String description;

    private Integer price;

}
