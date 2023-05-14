package com.shopIT.inventoryservice.controller;

import com.shopIT.inventoryservice.dto.InventoryDtoRequest;
import com.shopIT.inventoryservice.dto.InventoryDtoResponse;
import com.shopIT.inventoryservice.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/quantity/{skuCode}")
    public ResponseEntity<Integer> quantityInStock(@PathVariable String skuCode){
        Integer quantity = inventoryService.quantityInStock(skuCode);

        return ResponseEntity.status(HttpStatus.OK).body(quantity);
    }

    @PostMapping("/addInInventory")
    public ResponseEntity<String> addInInventory(@RequestBody InventoryDtoRequest inventoryDtoRequest){

        Integer id = inventoryService.addInInventory(inventoryDtoRequest);

        return ResponseEntity.status(HttpStatus.OK).body("Added in inventory with ID: " + id);
    }
}
