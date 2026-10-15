package application.data.contracts;

import application.data.entities.Cart;

import java.sql.SQLException;
import java.util.Optional;

public interface ICartRepository extends IRepository<Cart> {
    Optional<Cart> findActiveByBuyerId(Long buyerId) throws SQLException;
}
