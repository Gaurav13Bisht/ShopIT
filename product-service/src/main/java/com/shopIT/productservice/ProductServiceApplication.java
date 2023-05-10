package com.shopIT.productservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductServiceApplication.class, args);

		// Calling methods to test from here is not working, and we get the error regarding repository interface:
		//Exception in thread "main" java.lang.NullPointerException: Cannot invoke "com.shopIT.productservice.repository.ProductRepository.save(Object)" because "this.productRepo" is null
		//	at com.shopIT.productservice.service.ProductService.addProduct(ProductService.java:18)
		//	at com.shopIT.productservice.ProductServiceApplication.main(ProductServiceApplication.java:14)

		// So try to directly make controller class and test it through rest calls via postman.
	}
}