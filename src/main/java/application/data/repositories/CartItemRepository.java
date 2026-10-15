package application.data.repositories;

import application.data.contracts.ICartItemRepository;
import application.data.entities.CartItem;

import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class CartItemRepository extends BaseRepository<CartItem> implements ICartItemRepository {

    @Override
    protected String getTableName() {
        return "cart_item";
    }

    @Override
    protected CartItem mapRow(ResultSet row) throws SQLException {
        CartItem item = new CartItem();
        item.setId(row.getLong("id"));
        item.setCartId(toIdentifier(row, "cart_id"));
        item.setProductId(toIdentifier(row, "product_id"));
        item.setVariantId(toIdentifier(row, "variant_id"));
        item.setQuantity(row.getInt("quantity"));
        item.setUnitPrice(row.getBigDecimal("unit_price"));
        item.setSubtotal(row.getBigDecimal("subtotal"));
        return item;
    }

    @Override
    public CartItem save(CartItem entity) throws SQLException {
        String sql = "INSERT INTO cart_item (cart_id, product_id, variant_id, quantity, unit_price, subtotal) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        entity.setId(insertAndGetId(sql,
                entity.getCartId(),
                entity.getProductId(),
                entity.getVariantId(),
                entity.getQuantity(),
                entity.getUnitPrice(),
                entity.getSubtotal()));
        return entity;
    }

    @Override
    public boolean update(CartItem entity) throws SQLException {
        String sql = "UPDATE cart_item SET quantity = ?, unit_price = ?, subtotal = ? WHERE id = ?";

        return executeUpdate(sql,
                entity.getQuantity(),
                entity.getUnitPrice(),
                entity.getSubtotal(),
                entity.getId());
    }

    @Override
    public List<CartItem> findByCartId(Long cartId) throws SQLException {
        return queryList("SELECT * FROM cart_item WHERE cart_id = ? ORDER BY id", cartId);
    }

    @Override
    public Optional<CartItem> findByCartAndProduct(Long cartId, Long productId, Long variantId)
            throws SQLException {
        String sql = "SELECT * FROM cart_item WHERE cart_id = ? AND product_id = ? "
                + "AND variant_id IS NOT DISTINCT FROM ?";

        return queryOne(sql, cartId, productId, variantId);
    }

    @Override
    public boolean deleteByCartId(Long cartId) throws SQLException {
        return executeUpdate("DELETE FROM cart_item WHERE cart_id = ?", cartId);
    }
}
