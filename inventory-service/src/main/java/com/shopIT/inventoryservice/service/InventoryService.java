package com.shopIT.inventoryservice.service;

import com.shopIT.inventoryservice.dto.InventoryDtoRequest;
import com.shopIT.inventoryservice.entity.InventoryEntity;
import com.shopIT.inventoryservice.repository.InventoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepo;

    public Integer quantityInStock(String skuCode) {
        Optional<InventoryEntity> inventoryEntityOptional = inventoryRepo.findBySkuCode(skuCode);

        if(inventoryEntityOptional.isEmpty())
            return 0;
        else
            return inventoryEntityOptional.get().getQuantity();
    }

    public Integer addInInventory(InventoryDtoRequest inventoryDtoRequest) {

        Optional<InventoryEntity> inventoryEntityOptional = inventoryRepo.findBySkuCode(inventoryDtoRequest.getSkuCode());

        InventoryEntity inventoryEntity = null;

        if(inventoryEntityOptional.isEmpty()){
            inventoryEntity = InventoryEntity.builder()
                    .quantity(inventoryDtoRequest.getQuantity())
                    .skuCode(inventoryDtoRequest.getSkuCode())
                    .build();
        }
        else{
            inventoryEntity = inventoryEntityOptional.get();
            Integer prevQuantity = inventoryEntity.getQuantity();

            inventoryEntity.setQuantity(prevQuantity + inventoryDtoRequest.getQuantity());
        }

        inventoryRepo.save(inventoryEntity);

        log.info("Added/updated in inventory with id:" + inventoryEntity.getId());

        return inventoryEntity.getId();
    }
}