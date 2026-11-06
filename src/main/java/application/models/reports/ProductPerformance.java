package application.models.reports;

import application.data.views.ProductSalesRow;

public record ProductPerformance(ProductSalesRow sales, Classification classification, String suggestedAction) {

    public enum Classification {
        STAR,
        STEADY,
        SLOW,
        DEAD
    }
}
