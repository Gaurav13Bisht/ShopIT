package com.shopIT.inventoryservice.repository;

import com.shopIT.inventoryservice.entity.TPSEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TPSRepository extends JpaRepository<TPSEntity, String> {
    Optional<TPSEntity> findByUsername(String key);
}
