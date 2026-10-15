package application.data.contracts;

import application.data.enums.OrderStatus;
import application.data.entities.Order;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IOrderRepository extends IRepository<Order> {
    Optional<Order> findByOrderNumber(String orderNumber) throws SQLException;

    List<Order> findByBuyerId(Long buyerId) throws SQLException;

    List<Order> findByStatus(OrderStatus status) throws SQLException;
}
