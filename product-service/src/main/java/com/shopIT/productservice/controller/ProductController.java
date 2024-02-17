package com.shopIT.productservice.controller;

import com.shopIT.productservice.dto.ProductDtoRequest;
import com.shopIT.productservice.dto.ProductDtoResponse;
import com.shopIT.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private ProductService prodService;

    @Autowired
    public ProductController(ProductService prodService) {
        this.prodService = prodService;
    }

    @PostMapping("/addProduct")
    public ResponseEntity<String> addProduct(@RequestBody ProductDtoRequest productDtoRequest){
        int productId = prodService.addProduct(productDtoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Added the product with ID: " + productId);
    }

    @GetMapping("/getAllProducts")
    public ResponseEntity<List<ProductDtoResponse>> getAllProducts(){
        List<ProductDtoResponse> productDtoResponse = prodService.getAllProducts();
        return ResponseEntity.status(HttpStatus.OK).body(productDtoResponse);
    }
}