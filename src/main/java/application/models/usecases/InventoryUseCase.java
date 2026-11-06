package application.models.usecases;

import application.models.ports.in.IInventoryUseCase;
import application.models.services.InventoryService;
import application.data.enums.InventoryStatus;
import application.data.enums.MovementType;
import application.shared.exceptions.ResourceNotFoundException;
import application.data.ports.out.IInventoryMovementRepository;
import application.data.ports.out.IInventoryRepository;
import application.data.entities.Inventory;
import application.data.entities.InventoryMovement;
import application.data.entities.Product;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class InventoryUseCase implements IInventoryUseCase {

    private final InventoryService inventoryService;

    public InventoryUseCase(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Override
    public Inventory register(Long productId, Long variantId, Long warehouseId, int initialQuantity, Long userId) {
        return inventoryService.register(productId, variantId, warehouseId, initialQuantity, userId);
    }

    @Override
    public Inventory restock(Long productId, Long variantId, Long warehouseId, int quantity, Long userId) {
        return inventoryService.restock(productId, variantId, warehouseId, quantity, userId);
    }

    @Override
    public Inventory reserve(Long productId, Long variantId, Long warehouseId, int quantity, Long userId) {
        return inventoryService.reserve(productId, variantId, warehouseId, quantity, userId);
    }

    @Override
    public Inventory release(Long productId, Long variantId, Long warehouseId, int quantity, Long userId) {
        return inventoryService.release(productId, variantId, warehouseId, quantity, userId);
    }

    @Override
    public Inventory confirmSale(Long productId, Long variantId, Long warehouseId, int quantity, Long userId) {
        return inventoryService.confirmSale(productId, variantId, warehouseId, quantity, userId);
    }

    @Override
    public Inventory returnToStock(Long productId, Long variantId, Long warehouseId, int quantity, Long userId) {
        return inventoryService.returnToStock(productId, variantId, warehouseId, quantity, userId);
    }

    @Override
    public Inventory adjust(Long productId, Long variantId, Long warehouseId, int newQuantity, Long userId, String notes) {
        return inventoryService.adjust(productId, variantId, warehouseId, newQuantity, userId, notes);
    }

    @Override
    public Inventory markAsDamaged(Long productId, Long variantId, Long warehouseId, int quantity, Long userId) {
        return inventoryService.markAsDamaged(productId, variantId, warehouseId, quantity, userId);
    }

    @Override
    public int availableQuantity(Long productId, Long variantId, Long warehouseId) {
        return inventoryService.availableQuantity(productId, variantId, warehouseId);
    }

    @Override
    public boolean hasEnoughStock(Long productId, Long variantId, Long warehouseId, int quantity) {
        return inventoryService.hasEnoughStock(productId, variantId, warehouseId, quantity);
    }

    @Override
    public Inventory findById(Long inventoryId) {
        return inventoryService.findById(inventoryId);
    }

    @Override
    public List<Inventory> findByProduct(Long productId) {
        return inventoryService.findByProduct(productId);
    }

    @Override
    public List<Inventory> findByWarehouse(Long warehouseId) {
        return inventoryService.findByWarehouse(warehouseId);
    }

    @Override
    public List<InventoryMovement> movementHistory(Long inventoryId) {
        return inventoryService.movementHistory(inventoryId);
    }
}
