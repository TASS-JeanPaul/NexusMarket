package application.dialog.requests;

public record CartItemRequest(Long productId, Long variantId, int quantity) {
}
