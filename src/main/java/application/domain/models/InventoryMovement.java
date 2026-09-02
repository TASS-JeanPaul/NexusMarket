package application.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import application.domain.enums.MovementType;

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
