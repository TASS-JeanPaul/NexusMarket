package application.data.entities;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import application.data.enums.MovementType;

@Getter
@Setter
public class InventoryMovement {
    private Long id;

    private Long inventoryId;
    private MovementType type;
    private int quantity;
    private Long userId;
    private String notes;
    private LocalDateTime date;
}
