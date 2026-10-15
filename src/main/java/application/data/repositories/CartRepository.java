package application.data.repositories;

import application.data.contracts.ICartRepository;
import application.data.entities.Cart;

import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class CartRepository extends BaseRepository<Cart> implements ICartRepository {

    @Override
    protected String getTableName() {
        return "cart";
    }

    @Override
    protected Cart mapRow(ResultSet row) throws SQLException {
        Cart cart = new Cart();
        cart.setId(row.getLong("id"));
        cart.setBuyerId(toIdentifier(row, "buyer_id"));
        cart.setTotal(row.getBigDecimal("total"));
        cart.setActive(row.getBoolean("active"));
        cart.setCreatedAt(toDateTime(row.getTimestamp("created_at")));
        return cart;
    }

    @Override
    public Cart save(Cart entity) throws SQLException {
        String sql = "INSERT INTO cart (buyer_id, total, active, created_at) VALUES (?, ?, ?, ?)";

        entity.setId(insertAndGetId(sql,
                entity.getBuyerId(),
                entity.getTotal(),
                entity.isActive(),
                entity.getCreatedAt()));
        return entity;
    }

    @Override
    public boolean update(Cart entity) throws SQLException {
        return executeUpdate("UPDATE cart SET total = ?, active = ? WHERE id = ?",
                entity.getTotal(),
                entity.isActive(),
                entity.getId());
    }

    @Override
    public Optional<Cart> findActiveByBuyerId(Long buyerId) throws SQLException {
        return queryOne("SELECT * FROM cart WHERE buyer_id = ? AND active = TRUE ORDER BY id DESC LIMIT 1",
                buyerId);
    }
}
