package application.data.repositories;

import application.data.ports.out.IReportRepository;
import application.data.views.CarrierPerformanceRow;
import application.data.views.CategorySalesRow;
import application.data.views.OrderStatusRow;
import application.data.views.ProductReturnsRow;
import application.data.views.ProductSalesRow;
import application.data.views.SellerSalesRow;
import application.data.views.WarehouseStockRow;

import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ReportRepository extends BaseAnalyticsRepository implements IReportRepository {

    private static final String RETURNED_BY_LINE =
            "LEFT JOIN (SELECT order_detail_id, SUM(returned_quantity) AS returned "
                    + "FROM product_return_detail GROUP BY order_detail_id) r "
                    + "ON r.order_detail_id = od.id ";

    @Override
    public List<SellerSalesRow> salesBySeller(LocalDateTime since) throws SQLException {
        String sql = "SELECT s.id, s.business_name, "
                + "COUNT(DISTINCT o.id) AS orders, "
                + "COALESCE(SUM(od.quantity), 0) AS units_sold, "
                + "COALESCE(SUM(od.subtotal), 0) AS revenue, "
                + "COALESCE(SUM(r.returned), 0) AS units_returned "
                + "FROM seller s "
                + "JOIN product p ON p.seller_id = s.id "
                + "JOIN order_detail od ON od.product_id = p.id "
                + "JOIN orders o ON o.id = od.order_id "
                + RETURNED_BY_LINE
                + "WHERE o.status <> 'CANCELLED' AND o.created_at >= ? "
                + "GROUP BY s.id, s.business_name "
                + "ORDER BY revenue DESC";

        return query(sql, row -> new SellerSalesRow(
                row.getLong("id"),
                row.getString("business_name"),
                row.getLong("orders"),
                row.getLong("units_sold"),
                row.getBigDecimal("revenue"),
                row.getLong("units_returned")), Timestamp.valueOf(since));
    }

    @Override
    public List<CategorySalesRow> salesByCategory(LocalDateTime since) throws SQLException {
        String sql = "SELECT c.id, c.name, "
                + "COALESCE(SUM(od.quantity), 0) AS units_sold, "
                + "COALESCE(SUM(od.subtotal), 0) AS revenue "
                + "FROM category c "
                + "JOIN product p ON p.category_id = c.id "
                + "JOIN order_detail od ON od.product_id = p.id "
                + "JOIN orders o ON o.id = od.order_id "
                + "WHERE o.status <> 'CANCELLED' AND o.created_at >= ? "
                + "GROUP BY c.id, c.name "
                + "ORDER BY revenue DESC";

        return query(sql, row -> new CategorySalesRow(
                row.getLong("id"),
                row.getString("name"),
                row.getLong("units_sold"),
                row.getBigDecimal("revenue")), Timestamp.valueOf(since));
    }

    @Override
    public List<ProductSalesRow> salesByProduct(LocalDateTime since) throws SQLException {
        String sql = "SELECT p.id, p.code, p.name, p.type, c.name AS category_name, "
                + "COALESCE(SUM(CASE WHEN o.id IS NOT NULL THEN od.quantity ELSE 0 END), 0) AS units_sold, "
                + "COALESCE(SUM(CASE WHEN o.id IS NOT NULL THEN od.subtotal ELSE 0 END), 0) AS revenue, "
                + "COALESCE(SUM(CASE WHEN o.id IS NOT NULL THEN r.returned ELSE 0 END), 0) AS units_returned, "
                + "MAX(o.created_at) AS last_sold_at "
                + "FROM product p "
                + "JOIN category c ON c.id = p.category_id "
                + "LEFT JOIN order_detail od ON od.product_id = p.id "
                + "LEFT JOIN orders o ON o.id = od.order_id "
                + "AND o.status <> 'CANCELLED' AND o.created_at >= ? "
                + RETURNED_BY_LINE
                + "GROUP BY p.id, p.code, p.name, p.type, c.name "
                + "ORDER BY revenue DESC, p.name";

        return query(sql, row -> new ProductSalesRow(
                row.getLong("id"),
                row.getString("code"),
                row.getString("name"),
                row.getString("type"),
                row.getString("category_name"),
                row.getLong("units_sold"),
                row.getBigDecimal("revenue"),
                row.getLong("units_returned"),
                toDateTime(row.getTimestamp("last_sold_at"))), Timestamp.valueOf(since));
    }

    @Override
    public List<OrderStatusRow> ordersByStatus(LocalDateTime since) throws SQLException {
        String sql = "SELECT status, COUNT(*) AS orders, COALESCE(SUM(total), 0) AS total "
                + "FROM orders WHERE created_at >= ? "
                + "GROUP BY status ORDER BY orders DESC";

        return query(sql, row -> new OrderStatusRow(
                row.getString("status"),
                row.getLong("orders"),
                row.getBigDecimal("total")), Timestamp.valueOf(since));
    }

    @Override
    public List<WarehouseStockRow> stock() throws SQLException {
        return query(stockSql(""), this::mapStock);
    }

    @Override
    public List<WarehouseStockRow> stockByWarehouse(Long warehouseId) throws SQLException {
        return query(stockSql("WHERE w.id = ? "), this::mapStock, warehouseId);
    }

    @Override
    public List<ProductReturnsRow> returnsByProduct(LocalDateTime since) throws SQLException {
        String sql = "SELECT p.id, p.name, "
                + "COUNT(DISTINCT pr.id) AS return_count, "
                + "COALESCE(SUM(prd.returned_quantity), 0) AS units_returned "
                + "FROM product_return_detail prd "
                + "JOIN product_return pr ON pr.id = prd.return_id "
                + "JOIN order_detail od ON od.id = prd.order_detail_id "
                + "JOIN product p ON p.id = od.product_id "
                + "WHERE pr.requested_at >= ? "
                + "GROUP BY p.id, p.name "
                + "ORDER BY units_returned DESC";

        return query(sql, row -> new ProductReturnsRow(
                row.getLong("id"),
                row.getString("name"),
                row.getLong("return_count"),
                row.getLong("units_returned")), Timestamp.valueOf(since));
    }

    @Override
    public List<CarrierPerformanceRow> carrierPerformance(LocalDateTime since) throws SQLException {
        String sql = "SELECT sh.carrier, COUNT(*) AS shipments, "
                + "SUM(CASE WHEN sh.status = 'DELIVERED' THEN 1 ELSE 0 END) AS delivered, "
                + "SUM(CASE WHEN sh.status = 'FAILED' THEN 1 ELSE 0 END) AS failed "
                + "FROM shipment sh "
                + "JOIN orders o ON o.id = sh.order_id "
                + "WHERE o.created_at >= ? "
                + "GROUP BY sh.carrier ORDER BY shipments DESC";

        return query(sql, row -> new CarrierPerformanceRow(
                row.getString("carrier"),
                row.getLong("shipments"),
                row.getLong("delivered"),
                row.getLong("failed")), Timestamp.valueOf(since));
    }

    private String stockSql(String filter) {
        return "SELECT w.id AS warehouse_id, w.name AS warehouse_name, "
                + "p.id AS product_id, p.name AS product_name, p.type, "
                + "i.available_quantity, i.reserved_quantity, i.status "
                + "FROM inventory i "
                + "JOIN warehouse w ON w.id = i.warehouse_id "
                + "JOIN product p ON p.id = i.product_id "
                + filter
                + "ORDER BY w.name, p.name";
    }

    private WarehouseStockRow mapStock(java.sql.ResultSet row) throws SQLException {
        return new WarehouseStockRow(
                row.getLong("warehouse_id"),
                row.getString("warehouse_name"),
                row.getLong("product_id"),
                row.getString("product_name"),
                row.getString("type"),
                row.getInt("available_quantity"),
                row.getInt("reserved_quantity"),
                row.getString("status"));
    }
}
