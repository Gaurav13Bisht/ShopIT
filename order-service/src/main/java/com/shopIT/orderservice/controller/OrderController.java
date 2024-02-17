package com.shopIT.orderservice.controller;

import com.shopIT.orderservice.dto.OrderDtoRequest;
import com.shopIT.orderservice.dto.OrderDtoResponse;
import com.shopIT.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/placeOrder")
    public ResponseEntity<String> placeOrder(@RequestBody OrderDtoRequest orderDtoRequest){

        Integer orderId = orderService.placeOrder(orderDtoRequest);
        if(orderId == -2){
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Inventory service is not available right now. Please Try again later !!");
        }
        else {
            return ResponseEntity.status(HttpStatus.CREATED).body("Placed the order with ID: " + orderId);
        }
    }

    @GetMapping("/getOrderDetails/{orderId}")
    public ResponseEntity<OrderDtoResponse> getOrderDetails(@PathVariable Integer orderId){

        OrderDtoResponse orderDtoResponse = orderService.getOrderDetails(orderId);
        return ResponseEntity.status(HttpStatus.OK).body(orderDtoResponse);
    }
}