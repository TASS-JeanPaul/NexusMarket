package application.data.views;

public record CategorySalesRow(
        Long categoryId,
        String categoryName,
        long unitsSold,
        java.math.BigDecimal revenue) {
}
