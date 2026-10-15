package application.data.repositories;

import application.data.contracts.IOrderRepository;
import application.data.entities.Order;
import application.data.enums.OrderStatus;

import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepository extends BaseRepository<Order> implements IOrderRepository {

    @Override
    protected String getTableName() {
        return "orders";
    }

    @Override
    protected Order mapRow(ResultSet row) throws SQLException {
        Order order = new Order();
        order.setId(row.getLong("id"));
        order.setOrderNumber(row.getString("order_number"));
        order.setBuyerId(toIdentifier(row, "buyer_id"));
        order.setDeliveryAddressId(toIdentifier(row, "delivery_address_id"));
        order.setStatus(OrderStatus.valueOf(row.getString("status")));
        order.setTotal(row.getBigDecimal("total"));
        order.setCreatedAt(toDateTime(row.getTimestamp("created_at")));
        order.setCompletedAt(toDateTime(row.getTimestamp("completed_at")));
        return order;
    }

    @Override
    public Order save(Order entity) throws SQLException {
        String sql = "INSERT INTO orders "
                + "(order_number, buyer_id, delivery_address_id, status, total, created_at, completed_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        entity.setId(insertAndGetId(sql,
                entity.getOrderNumber(),
                entity.getBuyerId(),
                entity.getDeliveryAddressId(),
                entity.getStatus().name(),
                entity.getTotal(),
                entity.getCreatedAt(),
                entity.getCompletedAt()));
        return entity;
    }

    @Override
    public boolean update(Order entity) throws SQLException {
        String sql = "UPDATE orders SET delivery_address_id = ?, status = ?, total = ?, completed_at = ? "
                + "WHERE id = ?";

        return executeUpdate(sql,
                entity.getDeliveryAddressId(),
                entity.getStatus().name(),
                entity.getTotal(),
                entity.getCompletedAt(),
                entity.getId());
    }

    @Override
    public Optional<Order> findByOrderNumber(String orderNumber) throws SQLException {
        return queryOne("SELECT * FROM orders WHERE order_number = ?", orderNumber);
    }

    @Override
    public List<Order> findByBuyerId(Long buyerId) throws SQLException {
        return queryList("SELECT * FROM orders WHERE buyer_id = ? ORDER BY created_at DESC", buyerId);
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) throws SQLException {
        return queryList("SELECT * FROM orders WHERE status = ? ORDER BY created_at DESC", status.name());
    }
}
