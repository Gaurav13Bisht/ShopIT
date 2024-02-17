package com.shopIT.orderservice.service;

import com.shopIT.orderservice.dto.*;
import com.shopIT.orderservice.entity.OrderEntity;
import com.shopIT.orderservice.entity.OrderLineItemsEntity;
import com.shopIT.orderservice.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service    // Used to represent a class as business logic handling class and also mark this as @Component so spring IOC container
            // create and inject this class's object wherever it's needed like in controller class using @Autowired
@Slf4j     // Given by lombok for logging purpose
public class OrderService {

    private OrderRepository orderRepo;

    private WebClient.Builder webClientBuilder;

    @Autowired
    public OrderService(OrderRepository orderRepo, WebClient.Builder webClientBuilder) {
        this.orderRepo = orderRepo;
        this.webClientBuilder = webClientBuilder;
    }

    // Ideally, We do not use caching in case of POST operation.
    @CircuitBreaker(name="inventoryCall", fallbackMethod = "placeOrderFallBack")
    public Integer placeOrder(OrderDtoRequest orderDtoRequest) {

        // First check if the products with ordered quantity are in stock or not.
        // If they are in stock, place the order otherwise don't.

        // IMPORTANT:
        // A. If we loop through all the products/skuCodes in the order and check if they are in stock by calling the
        // inventory service one by one, it will be slower and costly.
        // B. So instead we will send all the skuCodes/products in single call to the inventory service and collect
        // the quantity available in stock for each product/skuCode in a list.

        List<String> skuCode = orderDtoRequest.getOrderLineItemsDtoRequestList().stream()
                .map(orderLineItem -> orderLineItem.getSkuCode()).toList();

        // Alternative of RestTemplate and introduced in Spring 5.
        // If we do not want load balancing then we can directly create webClient like below without making config class
//        WebClient webClient = WebClient.create();


        ResponseEntity<List<InventoryDtoResponse>> inventoryDtoResponseREntity = webClientBuilder.build()
                .get()
//                .uri("http://localhost:2334/shopIT/inventory/quantity", uriBuilder -> uriBuilder
                .uri("http://inventory-service/shopIT/inventory/quantity", uriBuilder -> uriBuilder
                        .queryParam("skuCode", skuCode)
                        .build())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<InventoryDtoResponse>>() {}) //Used when its returning-
                                                                    // -Response entity otherwise we use .bodyToMono()
                .block();

        log.info("Called inventory service from order service");   //provided by @Slf4j

        // Converting into stream for efficient filtering in next steps
        Stream<InventoryDtoResponse> inventoryDtoResponseStream = inventoryDtoResponseREntity.getBody().stream();


        // Since we called the inventory service only once and got all the required product's quantity
        // we need to now loop through the orderLineItem list in the order to check if each ordered product
        // with quantity 'x' is available in the inventory or not.

        // Here, orderLineItem contains ordered products with required quantity and
        // InventoryDtoResponse Stream contains all the ordered products with the available quantity
        for(OrderLineItemsDtoRequest orderLineItem : orderDtoRequest.getOrderLineItemsDtoRequestList()){
            Optional<InventoryDtoResponse> inventoryDtoResponseSame = inventoryDtoResponseStream.filter(inventoryDtoResponse -> {
                        return inventoryDtoResponse.getSkuCode().equals(orderLineItem.getSkuCode());
                    }) // Filtered to get the matching skuCode corresponding to the one in orderLineItem
                    .findFirst();  // to collect it

            // If inventoryDtoResponseSame is empty meaning the ordered product is not present in any inventory
            // or If the available quantity of any product is less than the required quantity in the order then
            // return exception as product(s) not in stock.
            if(inventoryDtoResponseSame.isEmpty() || inventoryDtoResponseSame.get().getQuantity() < orderLineItem.getQuantity()){
                log.info("Required product is not in stock.");   //provided by @Slf4j
                throw new IllegalArgumentException("Product(s) is out of stock !!!!!!!!");
            }
        }

        // Further statements will be executed only when all the products in order are present in the inventory with
        // the required quantity.

        // Placing the order
        OrderEntity orderEntity = OrderEntity.builder()
                .orderNumber(UUID.randomUUID().toString())   // Random number generation
                .build();

        List<OrderLineItemsEntity> orderLineItemsEntityList = orderDtoRequest.getOrderLineItemsDtoRequestList()
                .stream().map(orderLineItem -> orderLIDtoReqToOLIEntity(orderLineItem)).toList();

        orderEntity.setOrderLineItemsList(orderLineItemsEntityList);

        orderRepo.save(orderEntity);

        log.info("Placed order with ID {}", orderEntity.getId());   //provided by @Slf4j

        return orderEntity.getId();
    }

    public Integer placeOrderFallBack(OrderDtoRequest orderDtoRequest, RuntimeException runtimeException) {
        return -2;
    }


    // Cacheable Annotation includes:.
    // 1. key = "#orderId": Specifies the key used for caching. In this case, the orderId parameter value is
    //           used as the key for caching the method's results. This means that if the method is called with the same
    //           orderId value multiple times, the cached result will be returned instead of executing the method again.
    // 2. value or cacheName: attributes serve the same purpose: they specify the name of the cache where the method's
    //           results should be stored or retrieved. However, the value attribute is the preferred way to specify the cache name.
    // In this case, Serialized OrderDtoResponse objects would be stored in "orders" cache with the key "orderId".
    @Cacheable(key = "#orderId", value = "orders")
    public OrderDtoResponse getOrderDetails(Integer orderId) {
        Optional<OrderEntity> orderEntityOpt = orderRepo.findById(orderId);

        if(orderEntityOpt.isEmpty()) {
            return null;
        }

        OrderEntity orderEntity = orderEntityOpt.get();

        OrderDtoResponse orderDtoResponse = OrderDtoResponse.builder()
                .id(orderEntity.getId())
                .orderNumber(orderEntity.getOrderNumber())
                .build();

        List<OrderLineItemsDtoResponse> orderLineItemsDtoResponseList = orderEntity.getOrderLineItemsList()
                .stream().map(orderLineItem -> orderLIEntityToOLIDtoRes(orderLineItem)).toList();

        orderDtoResponse.setOrderLineItemsDtoResponseList(orderLineItemsDtoResponseList);

        return orderDtoResponse;
    }

    public OrderLineItemsDtoResponse orderLIEntityToOLIDtoRes(OrderLineItemsEntity orderLineItemsEntity){
        return OrderLineItemsDtoResponse.builder()
                .id(orderLineItemsEntity.getId())
                .price(orderLineItemsEntity.getPrice())
                .quantity(orderLineItemsEntity.getQuantity())
                .skuCode(orderLineItemsEntity.getSkuCode())
                .build();
    }

    public OrderLineItemsEntity orderLIDtoReqToOLIEntity(OrderLineItemsDtoRequest orderLineItemsDtoRequest){
        return OrderLineItemsEntity.builder()
                .skuCode(orderLineItemsDtoRequest.getSkuCode())
                .quantity(orderLineItemsDtoRequest.getQuantity())
                .price(orderLineItemsDtoRequest.getPrice())
                .build();
    }
}