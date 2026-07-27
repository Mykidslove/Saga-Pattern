package com.example.InventoryService.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.InventoryService.Dto.InventoryDto;
import com.example.InventoryService.Entity.Inventory;
import com.example.InventoryService.Repo.InventoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryServiceimpl {

    private final InventoryRepository inventoryRepository;

    public Inventory saveInventory(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    public InventoryDto checkStock(
            Long productId,
            Integer requiredQuantity) {

        Inventory inventory = inventoryRepository
                .findByProductId(productId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Inventory not found: " + productId
                        )
                );

        boolean inStock =
                inventory.getAvailableQuantity()
                        >= requiredQuantity;

        return new InventoryDto(
                productId,
                inventory.getAvailableQuantity(),
                inStock
        );
    }

    @Transactional
    public InventoryDto reserveInventory(
            Long productId,
            Integer requiredQuantity,
            Long orderId) {

        Inventory inventory = inventoryRepository
                .findByProductId(productId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Inventory not found: " + productId
                        )
                );

        if (requiredQuantity == null ||
                requiredQuantity <= 0) {

            throw new RuntimeException(
                    "Quantity must be greater than zero"
            );
        }

        if (inventory.getAvailableQuantity()
                < requiredQuantity) {

            throw new RuntimeException(
                    "Insufficient stock for product: "
                            + productId
            );
        }

        int remainingQuantity =
                inventory.getAvailableQuantity()
                        - requiredQuantity;

        inventory.setAvailableQuantity(
                remainingQuantity
        );

        inventoryRepository.save(inventory);

        return new InventoryDto(
                productId,
                remainingQuantity,
                true
        );
    }

    @Transactional
    public InventoryDto releaseInventory(
            Long productId,
            Integer quantity,
            Long orderId) {

        Inventory inventory = inventoryRepository
                .findByProductId(productId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Inventory not found: " + productId
                        )
                );

        if (quantity == null || quantity <= 0) {
            throw new RuntimeException(
                    "Quantity must be greater than zero"
            );
        }

        int updatedQuantity =
                inventory.getAvailableQuantity()
                        + quantity;

        inventory.setAvailableQuantity(
                updatedQuantity
        );

        inventoryRepository.save(inventory);

        return new InventoryDto(
                productId,
                updatedQuantity,
                true
        );
    }
}