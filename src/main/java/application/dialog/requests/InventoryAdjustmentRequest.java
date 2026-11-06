package application.dialog.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record InventoryAdjustmentRequest(
        @NotNull Long productId,
        Long variantId,
        @NotNull Long warehouseId,
        @PositiveOrZero int newQuantity,
        @NotNull Long userId,
        @NotBlank String notes) {
}
