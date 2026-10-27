package application.dialog.requests;

public record CheckoutRequest(Long buyerId, Long deliveryAddressId, Long warehouseId) {
}
