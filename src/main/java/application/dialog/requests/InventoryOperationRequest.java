package application.dialog.requests;

public record InventoryOperationRequest(
        Long productId,
        Long variantId,
        Long warehouseId,
        int quantity,
        Long userId) {
}
