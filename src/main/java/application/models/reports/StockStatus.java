package application.models.reports;

import application.data.views.WarehouseStockRow;

public record StockStatus(WarehouseStockRow stock, Level level) {

    public enum Level {
        OUT_OF_STOCK,
        CRITICAL,
        LOW,
        HEALTHY
    }
}
