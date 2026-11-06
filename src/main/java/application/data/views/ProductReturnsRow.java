package application.data.views;

public record ProductReturnsRow(
        Long productId,
        String productName,
        long returnCount,
        long unitsReturned) {
}
