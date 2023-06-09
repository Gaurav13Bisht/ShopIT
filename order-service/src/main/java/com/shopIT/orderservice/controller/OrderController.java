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

    @Autowired
    private OrderService orderService;

    @PostMapping("/placeOrder")
    public ResponseEntity<String> placeOrder(@RequestBody OrderDtoRequest orderDtoRequest){

        Integer order_id = orderService.placeOrder(orderDtoRequest);

        if(order_id == -2){
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Inventory service is not available right now. Please Try again later !!");
        }
        else
            return ResponseEntity.status(HttpStatus.CREATED).body("Placed the order with ID: " + order_id);
    }

    @GetMapping("/getOrderDetails/{order_id}")
    public ResponseEntity<OrderDtoResponse> getOrderDetails(@PathVariable Integer order_id){

        OrderDtoResponse orderDtoResponse = orderService.getOrderDetails(order_id);

        return ResponseEntity.status(HttpStatus.OK).body(orderDtoResponse);
    }
}
