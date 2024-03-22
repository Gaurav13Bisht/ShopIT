package com.shopIT.orderservice.repository;

import com.shopIT.orderservice.entity.TPSEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TPSRepository extends JpaRepository<TPSEntity, String> {
    Optional<TPSEntity> findByUsername(String username);
}
