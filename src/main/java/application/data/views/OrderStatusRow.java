package application.data.views;

public record OrderStatusRow(
        String status,
        long orders,
        java.math.BigDecimal total) {
}
