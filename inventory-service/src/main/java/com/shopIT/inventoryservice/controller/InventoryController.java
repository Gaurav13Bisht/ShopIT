package com.shopIT.inventoryservice.controller;

import com.shopIT.inventoryservice.dto.InventoryDtoRequest;
import com.shopIT.inventoryservice.dto.InventoryDtoResponse;
import com.shopIT.inventoryservice.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    //Using @RequestParam for multiple inputs
    @GetMapping("/quantity")
    public ResponseEntity<List<InventoryDtoResponse>> quantityInStock(@RequestParam List<String> skuCode){
        List<InventoryDtoResponse> inventoryDtoResponseList = inventoryService.quantityInStock(skuCode);

        return ResponseEntity.status(HttpStatus.OK).body(inventoryDtoResponseList);
    }

    @PostMapping("/addInInventory")
    public ResponseEntity<String> addInInventory(@RequestBody InventoryDtoRequest inventoryDtoRequest){

        Integer id = inventoryService.addInInventory(inventoryDtoRequest);

        return ResponseEntity.status(HttpStatus.OK).body("Added in inventory with ID: " + id);
    }
}
