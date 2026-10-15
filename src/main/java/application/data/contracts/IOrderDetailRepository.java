package application.data.contracts;

import application.data.entities.OrderDetail;

import java.sql.SQLException;
import java.util.List;

public interface IOrderDetailRepository extends IRepository<OrderDetail> {
    List<OrderDetail> findByOrderId(Long orderId) throws SQLException;
}
