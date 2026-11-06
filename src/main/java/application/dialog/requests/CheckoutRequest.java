package application.dialog.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record CheckoutRequest(@NotNull Long buyerId, Long deliveryAddressId, Long warehouseId) {
}
