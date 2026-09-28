package application.domain.interfaces;

import application.domain.models.CartItem;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ICartItemRepository extends IRepository<CartItem> {
    List<CartItem> findByCartId(Long cartId) throws SQLException;

    Optional<CartItem> findByCartAndProduct(Long cartId, Long productId, Long variantId) throws SQLException;

    boolean deleteByCartId(Long cartId) throws SQLException;
}
