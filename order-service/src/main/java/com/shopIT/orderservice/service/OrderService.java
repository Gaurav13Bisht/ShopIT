package com.shopIT.orderservice.service;

import com.shopIT.orderservice.dto.OrderDtoRequest;
import com.shopIT.orderservice.dto.OrderDtoResponse;
import com.shopIT.orderservice.dto.OrderLineItemsDtoRequest;
import com.shopIT.orderservice.dto.OrderLineItemsDtoResponse;
import com.shopIT.orderservice.entity.OrderEntity;
import com.shopIT.orderservice.entity.OrderLineItemsEntity;
import com.shopIT.orderservice.repository.OrderRepository;
import jakarta.persistence.criteria.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service    // Used to represent a class as business logic handling class and also mark this as @Component so spring IOC container
            // create and inject this class's object wherever it's needed like in controller class using @Autowired
@Slf4j     // Given by lombok for logging purpose
public class OrderService {
    @Autowired
    private OrderRepository orderRepo;

    public Integer placeOrder(OrderDtoRequest orderDtoRequest) {
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

    public OrderLineItemsEntity orderLIDtoReqToOLIEntity(OrderLineItemsDtoRequest orderLineItemsDtoRequest){
        OrderLineItemsEntity orderLineItemsEntity = OrderLineItemsEntity.builder()
                .skuCode(orderLineItemsDtoRequest.getSkuCode())
                .quantity(orderLineItemsDtoRequest.getQuantity())
                .price(orderLineItemsDtoRequest.getPrice())
                .build();

        return orderLineItemsEntity;
    }

    public OrderDtoResponse getOrderDetails(Integer orderId) {
        Optional<OrderEntity> orderEntityOpt = orderRepo.findById(orderId);
        if(orderEntityOpt.isEmpty())
            return null;

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
}