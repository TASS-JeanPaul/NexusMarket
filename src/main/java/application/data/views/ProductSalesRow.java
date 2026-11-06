package application.data.views;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductSalesRow(
        Long productId,
        String code,
        String name,
        String type,
        String categoryName,
        long unitsSold,
        BigDecimal revenue,
        long unitsReturned,
        LocalDateTime lastSoldAt) {
}
