package application.data.entities;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import application.data.enums.InventoryStatus;

@Getter
@Setter
public class Inventory {
    private Long id;
    private Long productId;
    private Long variantId;
    private Long warehouseId;
    private int availableQuantity;
    private int reservedQuantity;
    private InventoryStatus status;
    private LocalDateTime updatedAt;
}
