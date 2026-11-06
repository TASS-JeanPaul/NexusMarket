package application.data.views;

public record WarehouseStockRow(
        Long warehouseId,
        String warehouseName,
        Long productId,
        String productName,
        String productType,
        int available,
        int reserved,
        String status) {
}
