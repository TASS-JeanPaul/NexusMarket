package application.data.ports.out;

import application.data.views.CarrierPerformanceRow;
import application.data.views.CategorySalesRow;
import application.data.views.OrderStatusRow;
import application.data.views.ProductReturnsRow;
import application.data.views.ProductSalesRow;
import application.data.views.SellerSalesRow;
import application.data.views.WarehouseStockRow;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public interface IReportRepository {

    List<SellerSalesRow> salesBySeller(LocalDateTime since) throws SQLException;

    List<CategorySalesRow> salesByCategory(LocalDateTime since) throws SQLException;

    List<ProductSalesRow> salesByProduct(LocalDateTime since) throws SQLException;

    List<OrderStatusRow> ordersByStatus(LocalDateTime since) throws SQLException;

    List<WarehouseStockRow> stock() throws SQLException;

    List<WarehouseStockRow> stockByWarehouse(Long warehouseId) throws SQLException;

    List<ProductReturnsRow> returnsByProduct(LocalDateTime since) throws SQLException;

    List<CarrierPerformanceRow> carrierPerformance(LocalDateTime since) throws SQLException;
}
