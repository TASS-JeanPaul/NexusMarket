package application.data.contracts;

import application.data.entities.ProductReturn;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IProductReturnRepository extends IRepository<ProductReturn> {
    Optional<ProductReturn> findByReturnNumber(String returnNumber) throws SQLException;

    List<ProductReturn> findByOrderId(Long orderId) throws SQLException;

    List<ProductReturn> findByBuyerId(Long buyerId) throws SQLException;
}
