package application.models.ports.in;

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

public interface IInventoryUseCase {

    Inventory register(Long productId, Long variantId, Long warehouseId, int initialQuantity, Long userId);

    Inventory restock(Long productId, Long variantId, Long warehouseId, int quantity, Long userId);

    Inventory reserve(Long productId, Long variantId, Long warehouseId, int quantity, Long userId);

    Inventory release(Long productId, Long variantId, Long warehouseId, int quantity, Long userId);

    Inventory confirmSale(Long productId, Long variantId, Long warehouseId, int quantity, Long userId);

    Inventory returnToStock(Long productId, Long variantId, Long warehouseId, int quantity, Long userId);

    Inventory adjust(Long productId, Long variantId, Long warehouseId, int newQuantity, Long userId, String notes);

    Inventory markAsDamaged(Long productId, Long variantId, Long warehouseId, int quantity, Long userId);

    int availableQuantity(Long productId, Long variantId, Long warehouseId);

    boolean hasEnoughStock(Long productId, Long variantId, Long warehouseId, int quantity);

    Inventory findById(Long inventoryId);

    List<Inventory> findByProduct(Long productId);

    List<Inventory> findByWarehouse(Long warehouseId);

    List<InventoryMovement> movementHistory(Long inventoryId);
}
