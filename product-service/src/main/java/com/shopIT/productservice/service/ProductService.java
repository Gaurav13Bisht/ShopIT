package com.shopIT.productservice.service;

import com.shopIT.productservice.dto.ProductDtoRequest;
import com.shopIT.productservice.dto.ProductDtoResponse;
import com.shopIT.productservice.entity.ProductEntity;
import com.shopIT.productservice.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j     // Given by lombok for logging purpose
public class ProductService {

    @Autowired
    private ProductRepository productRepo;
    public Integer addProduct(ProductDtoRequest productDtoRequest){
        ProductEntity productEntity = productDtoReqToEntity(productDtoRequest);

        productRepo.save(productEntity);
        log.info("Created product with ID {}", productEntity.getProduct_id());   //provided by @Slf4j

        return productEntity.getProduct_id();
    }

    public List<ProductDtoResponse> getAllProducts() {
        List<ProductEntity> productEntity = productRepo.findAll();

        //Using Stream for conversion in less LOC
        //List<ProductDtoResponse> productDtoResponses = productEntity.stream().map(product -> productEntityToDtoRes(product)).toList();

        //Can be written like this:
        List<ProductDtoResponse> productDtoResponses = productEntity.stream().map(this::productEntityToDtoRes).toList();

        return productDtoResponses;
    }

    public ProductEntity productDtoReqToEntity(ProductDtoRequest productDtoRequest){
        // Using builder() provided by lombok to implement conversion in less LOC
        ProductEntity productEntity = ProductEntity.builder()
                .product_name(productDtoRequest.getProduct_name())
                .price(productDtoRequest.getPrice())
                .description(productDtoRequest.getDescription())
                .build();

        return productEntity;
    }


    public ProductDtoResponse productEntityToDtoRes(ProductEntity productEntity){
        // Using builder() provided by lombok to implement conversion in less LOC
        ProductDtoResponse productDtoResponse = ProductDtoResponse.builder()
                .product_id(productEntity.getProduct_id())
                .product_name(productEntity.getProduct_name())
                .price(productEntity.getPrice())
                .description(productEntity.getDescription())
                .build();

        return productDtoResponse;
    }
}
