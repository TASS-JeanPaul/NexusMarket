package application.data.repositories;

import application.data.contracts.IProductReturnRepository;
import application.data.entities.ProductReturn;
import application.data.enums.ReturnStatus;

import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductReturnRepository extends BaseRepository<ProductReturn>
        implements IProductReturnRepository {

    @Override
    protected String getTableName() {
        return "product_return";
    }

    @Override
    protected ProductReturn mapRow(ResultSet row) throws SQLException {
        ProductReturn productReturn = new ProductReturn();
        productReturn.setId(row.getLong("id"));
        productReturn.setReturnNumber(row.getString("return_number"));
        productReturn.setOrderId(toIdentifier(row, "order_id"));
        productReturn.setBuyerId(toIdentifier(row, "buyer_id"));
        productReturn.setReason(row.getString("reason"));
        productReturn.setStatus(ReturnStatus.valueOf(row.getString("status")));
        productReturn.setReviewedById(toIdentifier(row, "reviewed_by_id"));
        productReturn.setRequestedAt(toDateTime(row.getTimestamp("requested_at")));
        productReturn.setClosedAt(toDateTime(row.getTimestamp("closed_at")));
        return productReturn;
    }

    @Override
    public ProductReturn save(ProductReturn entity) throws SQLException {
        String sql = "INSERT INTO product_return "
                + "(return_number, order_id, buyer_id, reason, status, reviewed_by_id, requested_at, closed_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        entity.setId(insertAndGetId(sql,
                entity.getReturnNumber(),
                entity.getOrderId(),
                entity.getBuyerId(),
                entity.getReason(),
                entity.getStatus().name(),
                entity.getReviewedById(),
                entity.getRequestedAt(),
                entity.getClosedAt()));
        return entity;
    }

    @Override
    public boolean update(ProductReturn entity) throws SQLException {
        String sql = "UPDATE product_return SET status = ?, reviewed_by_id = ?, closed_at = ? WHERE id = ?";

        return executeUpdate(sql,
                entity.getStatus().name(),
                entity.getReviewedById(),
                entity.getClosedAt(),
                entity.getId());
    }

    @Override
    public Optional<ProductReturn> findByReturnNumber(String returnNumber) throws SQLException {
        return queryOne("SELECT * FROM product_return WHERE return_number = ?", returnNumber);
    }

    @Override
    public List<ProductReturn> findByOrderId(Long orderId) throws SQLException {
        return queryList("SELECT * FROM product_return WHERE order_id = ? ORDER BY requested_at DESC", orderId);
    }

    @Override
    public List<ProductReturn> findByBuyerId(Long buyerId) throws SQLException {
        return queryList("SELECT * FROM product_return WHERE buyer_id = ? ORDER BY requested_at DESC", buyerId);
    }
}
