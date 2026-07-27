package com.example.InventoryService.Repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.InventoryService.Entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory,Long> {

	Optional<Inventory> findByProductId(Long productId);

}
