package application.data.views;

public record SellerSalesRow(
        Long sellerId,
        String businessName,
        long orders,
        long unitsSold,
        java.math.BigDecimal revenue,
        long unitsReturned) {
}
