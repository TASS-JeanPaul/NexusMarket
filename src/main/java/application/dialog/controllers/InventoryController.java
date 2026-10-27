package application.dialog.controllers;

import application.data.entities.Inventory;
import application.data.entities.InventoryMovement;
import application.dialog.requests.InventoryAdjustmentRequest;
import application.dialog.requests.InventoryOperationRequest;
import application.dialog.requests.InventoryRegistrationRequest;
import application.models.services.InventoryService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Inventory register(@RequestBody InventoryRegistrationRequest request) {
        return inventoryService.register(
                request.productId(),
                request.variantId(),
                request.warehouseId(),
                request.initialQuantity(),
                request.userId());
    }

    @GetMapping
    public List<Inventory> find(@RequestParam(required = false) Long productId,
                                @RequestParam(required = false) Long warehouseId) {
        if (productId != null) {
            return inventoryService.findByProduct(productId);
        }
        return inventoryService.findByWarehouse(warehouseId);
    }

    @GetMapping("/{id}")
    public Inventory findById(@PathVariable Long id) {
        return inventoryService.findById(id);
    }

    @GetMapping("/{id}/movements")
    public List<InventoryMovement> movementHistory(@PathVariable Long id) {
        return inventoryService.movementHistory(id);
    }

    @PostMapping("/restocks")
    public Inventory restock(@RequestBody InventoryOperationRequest request) {
        return inventoryService.restock(
                request.productId(),
                request.variantId(),
                request.warehouseId(),
                request.quantity(),
                request.userId());
    }

    @PostMapping("/reservations")
    public Inventory reserve(@RequestBody InventoryOperationRequest request) {
        return inventoryService.reserve(
                request.productId(),
                request.variantId(),
                request.warehouseId(),
                request.quantity(),
                request.userId());
    }

    @PostMapping("/releases")
    public Inventory release(@RequestBody InventoryOperationRequest request) {
        return inventoryService.release(
                request.productId(),
                request.variantId(),
                request.warehouseId(),
                request.quantity(),
                request.userId());
    }

    @PostMapping("/returns")
    public Inventory returnToStock(@RequestBody InventoryOperationRequest request) {
        return inventoryService.returnToStock(
                request.productId(),
                request.variantId(),
                request.warehouseId(),
                request.quantity(),
                request.userId());
    }

    @PostMapping("/damages")
    public Inventory markAsDamaged(@RequestBody InventoryOperationRequest request) {
        return inventoryService.markAsDamaged(
                request.productId(),
                request.variantId(),
                request.warehouseId(),
                request.quantity(),
                request.userId());
    }

    @PostMapping("/adjustments")
    public Inventory adjust(@RequestBody InventoryAdjustmentRequest request) {
        return inventoryService.adjust(
                request.productId(),
                request.variantId(),
                request.warehouseId(),
                request.newQuantity(),
                request.userId(),
                request.notes());
    }
}
