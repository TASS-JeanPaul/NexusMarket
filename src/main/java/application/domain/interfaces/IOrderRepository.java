package application.domain.interfaces;

import application.domain.enums.OrderStatus;
import application.domain.models.Order;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IOrderRepository extends IRepository<Order> {
    Optional<Order> findByOrderNumber(String orderNumber) throws SQLException;

    List<Order> findByBuyerId(Long buyerId) throws SQLException;

    List<Order> findByStatus(OrderStatus status) throws SQLException;
}
