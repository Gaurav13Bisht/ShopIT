package com.shopIT.inventoryservice.repository;

import com.shopIT.inventoryservice.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<InventoryEntity, Integer> {
    Optional<InventoryEntity> findBySkuCode(String skuCode);
}
