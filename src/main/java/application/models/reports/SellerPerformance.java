package application.models.reports;

import application.data.views.SellerSalesRow;

import java.math.BigDecimal;

public record SellerPerformance(SellerSalesRow sales, BigDecimal returnRatePercent, Standing standing) {

    public enum Standing {
        EXCELLENT,
        SOLID,
        WATCH,
        AT_RISK
    }
}
