package application.dialog.requests;

public record InventoryAdjustmentRequest(
        Long productId,
        Long variantId,
        Long warehouseId,
        int newQuantity,
        Long userId,
        String notes) {
}
