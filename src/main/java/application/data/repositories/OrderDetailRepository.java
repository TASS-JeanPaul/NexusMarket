package application.data.repositories;

import application.data.contracts.IOrderDetailRepository;
import application.data.entities.OrderDetail;

import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class OrderDetailRepository extends BaseRepository<OrderDetail> implements IOrderDetailRepository {

    @Override
    protected String getTableName() {
        return "order_detail";
    }

    @Override
    protected OrderDetail mapRow(ResultSet row) throws SQLException {
        OrderDetail detail = new OrderDetail();
        detail.setId(row.getLong("id"));
        detail.setOrderId(toIdentifier(row, "order_id"));
        detail.setProductId(toIdentifier(row, "product_id"));
        detail.setVariantId(toIdentifier(row, "variant_id"));
        detail.setProductName(row.getString("product_name"));
        detail.setQuantity(row.getInt("quantity"));
        detail.setUnitPrice(row.getBigDecimal("unit_price"));
        detail.setSubtotal(row.getBigDecimal("subtotal"));
        return detail;
    }

    @Override
    public OrderDetail save(OrderDetail entity) throws SQLException {
        String sql = "INSERT INTO order_detail "
                + "(order_id, product_id, variant_id, product_name, quantity, unit_price, subtotal) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        entity.setId(insertAndGetId(sql,
                entity.getOrderId(),
                entity.getProductId(),
                entity.getVariantId(),
                entity.getProductName(),
                entity.getQuantity(),
                entity.getUnitPrice(),
                entity.getSubtotal()));
        return entity;
    }

    @Override
    public boolean update(OrderDetail entity) throws SQLException {
        String sql = "UPDATE order_detail SET quantity = ?, unit_price = ?, subtotal = ? WHERE id = ?";

        return executeUpdate(sql,
                entity.getQuantity(),
                entity.getUnitPrice(),
                entity.getSubtotal(),
                entity.getId());
    }

    @Override
    public List<OrderDetail> findByOrderId(Long orderId) throws SQLException {
        return queryList("SELECT * FROM order_detail WHERE order_id = ? ORDER BY id", orderId);
    }
}
