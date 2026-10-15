package application.models.services;

import application.data.enums.InventoryStatus;
import application.data.enums.MovementType;
import application.shared.exceptions.ResourceNotFoundException;
import application.data.contracts.IInventoryMovementRepository;
import application.data.contracts.IInventoryRepository;
import application.data.entities.Inventory;
import application.data.entities.InventoryMovement;
import application.data.entities.Product;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryService extends BaseService {

    private final IInventoryRepository inventoryRepository;
    private final IInventoryMovementRepository movementRepository;
    private final CatalogService catalogService;
    private final WarehouseService warehouseService;
    private final UserService userService;

    public InventoryService(IInventoryRepository inventoryRepository,
                            IInventoryMovementRepository movementRepository,
                            CatalogService catalogService,
                            WarehouseService warehouseService,
                            UserService userService) {
        this.inventoryRepository = inventoryRepository;
        this.movementRepository = movementRepository;
        this.catalogService = catalogService;
        this.warehouseService = warehouseService;
        this.userService = userService;
    }

    public Inventory register(Long productId, Long variantId, Long warehouseId, int initialQuantity, Long userId) {
        return execute(() -> {
            requireNotNegative(initialQuantity, "The initial quantity");
            requireStockableProduct(productId);
            warehouseService.requireActive(warehouseId);
            userService.findActiveOrFail(userId);
            require(inventoryRepository.findByProductVariantAndWarehouse(productId, variantId, warehouseId).isEmpty(),
                    "The product " + productId + " already has inventory in the warehouse " + warehouseId);

            Inventory inventory = new Inventory();
            inventory.setProductId(productId);
            inventory.setVariantId(variantId);
            inventory.setWarehouseId(warehouseId);
            inventory.setAvailableQuantity(initialQuantity);
            inventory.setReservedQuantity(0);
            inventory.setStatus(InventoryStatus.AVAILABLE);
            inventory.setUpdatedAt(LocalDateTime.now());

            Inventory stored = inventoryRepository.save(inventory);
            recordMovement(stored, MovementType.INBOUND, initialQuantity, userId, "Initial inventory registration");
            return stored;
        });
    }

    public Inventory restock(Long productId, Long variantId, Long warehouseId, int quantity, Long userId) {
        return execute(() -> {
            requirePositive(quantity, "The quantity to receive");
            userService.findActiveOrFail(userId);

            Inventory inventory = findRecordOrFail(productId, variantId, warehouseId);
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() + quantity);

            return applyChange(inventory, MovementType.INBOUND, quantity, userId, "Merchandise inbound");
        });
    }

    public Inventory reserve(Long productId, Long variantId, Long warehouseId, int quantity, Long userId) {
        return execute(() -> {
            requirePositive(quantity, "The quantity to reserve");
            userService.findActiveOrFail(userId);

            Inventory inventory = findRecordOrFail(productId, variantId, warehouseId);
            require(inventory.getStatus() != InventoryStatus.DAMAGED,
                    "The inventory of the product " + productId + " is marked as damaged and cannot be reserved");
            require(inventory.getAvailableQuantity() >= quantity,
                    "Insufficient stock for the product " + productId + " in the warehouse " + warehouseId
                            + ": available " + inventory.getAvailableQuantity() + ", requested " + quantity);

            inventory.setAvailableQuantity(inventory.getAvailableQuantity() - quantity);
            inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);

            return applyChange(inventory, MovementType.RESERVATION, quantity, userId, "Reservation for purchase");
        });
    }

    public Inventory release(Long productId, Long variantId, Long warehouseId, int quantity, Long userId) {
        return execute(() -> {
            requirePositive(quantity, "The quantity to release");
            userService.findActiveOrFail(userId);

            Inventory inventory = findRecordOrFail(productId, variantId, warehouseId);
            require(inventory.getReservedQuantity() >= quantity,
                    "Only " + inventory.getReservedQuantity() + " units are reserved for the product " + productId
                            + ", so " + quantity + " units cannot be released");

            inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() + quantity);

            return applyChange(inventory, MovementType.ADJUSTMENT, quantity, userId, "Release of a reservation");
        });
    }

    public Inventory confirmSale(Long productId, Long variantId, Long warehouseId, int quantity, Long userId) {
        return execute(() -> {
            requirePositive(quantity, "The quantity sold");
            userService.findActiveOrFail(userId);

            Inventory inventory = findRecordOrFail(productId, variantId, warehouseId);
            require(inventory.getReservedQuantity() >= quantity,
                    "The sale cannot be confirmed: only " + inventory.getReservedQuantity()
                            + " units are reserved for the product " + productId);

            inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);

            return applyChange(inventory, MovementType.SALE_OUTBOUND, quantity, userId, "Outbound for sale");
        });
    }

    public Inventory returnToStock(Long productId, Long variantId, Long warehouseId, int quantity, Long userId) {
        return execute(() -> {
            requirePositive(quantity, "The quantity returned");
            userService.findActiveOrFail(userId);

            Inventory inventory = findRecordOrFail(productId, variantId, warehouseId);
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() + quantity);

            return applyChange(inventory, MovementType.RETURN, quantity, userId, "Inbound from a return");
        });
    }

    public Inventory adjust(Long productId, Long variantId, Long warehouseId, int newQuantity, Long userId,
                            String notes) {
        return execute(() -> {
            requireNotNegative(newQuantity, "The adjusted quantity");
            requireText(notes, "The reason for the adjustment");
            userService.findActiveOrFail(userId);

            Inventory inventory = findRecordOrFail(productId, variantId, warehouseId);
            int difference = Math.abs(newQuantity - inventory.getAvailableQuantity());
            inventory.setAvailableQuantity(newQuantity);

            return applyChange(inventory, MovementType.ADJUSTMENT, difference, userId, notes);
        });
    }

    public Inventory markAsDamaged(Long productId, Long variantId, Long warehouseId, int quantity, Long userId) {
        return execute(() -> {
            requirePositive(quantity, "The damaged quantity");
            userService.findActiveOrFail(userId);

            Inventory inventory = findRecordOrFail(productId, variantId, warehouseId);
            require(inventory.getAvailableQuantity() >= quantity,
                    "Only " + inventory.getAvailableQuantity() + " units are available, so " + quantity
                            + " units cannot be marked as damaged");

            inventory.setAvailableQuantity(inventory.getAvailableQuantity() - quantity);
            inventory.setUpdatedAt(LocalDateTime.now());
            inventory.setStatus(InventoryStatus.DAMAGED);
            inventoryRepository.update(inventory);
            recordMovement(inventory, MovementType.ADJUSTMENT, quantity, userId, "Merchandise marked as damaged");
            return inventory;
        });
    }

    public int availableQuantity(Long productId, Long variantId, Long warehouseId) {
        return findRecordOrFail(productId, variantId, warehouseId).getAvailableQuantity();
    }

    public boolean hasEnoughStock(Long productId, Long variantId, Long warehouseId, int quantity) {
        return execute(() -> inventoryRepository
                .findByProductVariantAndWarehouse(productId, variantId, warehouseId)
                .map(inventory -> inventory.getAvailableQuantity() >= quantity)
                .orElse(false));
    }

    public Inventory findById(Long inventoryId) {
        requireIdentifier(inventoryId, "The inventory identifier");
        return execute(() -> findOrFail(inventoryRepository.findById(inventoryId), "Inventory", inventoryId));
    }

    public List<Inventory> findByProduct(Long productId) {
        requireIdentifier(productId, "The product identifier");
        return execute(() -> inventoryRepository.findByProductId(productId));
    }

    public List<Inventory> findByWarehouse(Long warehouseId) {
        requireIdentifier(warehouseId, "The warehouse identifier");
        return execute(() -> inventoryRepository.findByWarehouseId(warehouseId));
    }

    public List<InventoryMovement> movementHistory(Long inventoryId) {
        findById(inventoryId);
        return execute(() -> movementRepository.findByInventoryId(inventoryId));
    }

    private Product requireStockableProduct(Long productId) {
        Product product = catalogService.findProductById(productId);
        require(product.managesInventory(),
                "The product " + productId + " does not manage inventory because it is delivered without dispatch");
        return product;
    }

    private Inventory findRecordOrFail(Long productId, Long variantId, Long warehouseId) {
        requireIdentifier(productId, "The product identifier");
        requireIdentifier(warehouseId, "The warehouse identifier");
        return execute(() -> inventoryRepository
                .findByProductVariantAndWarehouse(productId, variantId, warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "The product " + productId + " has no inventory registered in the warehouse " + warehouseId)));
    }

    private Inventory applyChange(Inventory inventory, MovementType type, int quantity, Long userId, String notes) {
        return execute(() -> {
            requireNotNegative(inventory.getAvailableQuantity(), "The available quantity");
            requireNotNegative(inventory.getReservedQuantity(), "The reserved quantity");

            inventory.setUpdatedAt(LocalDateTime.now());
            inventory.setStatus(resolveStatus(inventory));
            inventoryRepository.update(inventory);
            recordMovement(inventory, type, quantity, userId, notes);
            return inventory;
        });
    }

    private InventoryStatus resolveStatus(Inventory inventory) {
        if (inventory.getStatus() == InventoryStatus.DAMAGED) {
            return InventoryStatus.DAMAGED;
        }
        if (inventory.getAvailableQuantity() == 0 && inventory.getReservedQuantity() > 0) {
            return InventoryStatus.RESERVED;
        }
        return InventoryStatus.AVAILABLE;
    }

    private void recordMovement(Inventory inventory, MovementType type, int quantity, Long userId, String notes) {
        execute(() -> {
            InventoryMovement movement = new InventoryMovement();
            movement.setInventoryId(inventory.getId());
            movement.setType(type);
            movement.setQuantity(quantity);
            movement.setUserId(userId);
            movement.setNotes(notes);
            movement.setDate(LocalDateTime.now());

            return movementRepository.save(movement);
        });
    }
}
