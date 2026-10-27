package application.dialog.requests;

public record InventoryRegistrationRequest(
        Long productId,
        Long variantId,
        Long warehouseId,
        int initialQuantity,
        Long userId) {
}
