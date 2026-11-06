package application.dialog.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record ShipmentRequest(
        @NotNull Long orderId,
        @NotNull Long sourceWarehouseId,
        @NotNull Long logisticsOperatorId,
        @NotBlank String carrier,
        @NotBlank String deliveryAddress) {
}
