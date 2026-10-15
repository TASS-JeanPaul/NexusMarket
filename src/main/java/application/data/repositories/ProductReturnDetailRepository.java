package application.data.repositories;

import application.data.contracts.IProductReturnDetailRepository;
import application.data.entities.ProductReturnDetail;

import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ProductReturnDetailRepository extends BaseRepository<ProductReturnDetail>
        implements IProductReturnDetailRepository {

    @Override
    protected String getTableName() {
        return "product_return_detail";
    }

    @Override
    protected ProductReturnDetail mapRow(ResultSet row) throws SQLException {
        ProductReturnDetail detail = new ProductReturnDetail();
        detail.setId(row.getLong("id"));
        detail.setReturnId(toIdentifier(row, "return_id"));
        detail.setOrderDetailId(toIdentifier(row, "order_detail_id"));
        detail.setReturnedQuantity(row.getInt("returned_quantity"));
        detail.setNotes(row.getString("notes"));
        return detail;
    }

    @Override
    public ProductReturnDetail save(ProductReturnDetail entity) throws SQLException {
        String sql = "INSERT INTO product_return_detail (return_id, order_detail_id, returned_quantity, notes) "
                + "VALUES (?, ?, ?, ?)";

        entity.setId(insertAndGetId(sql,
                entity.getReturnId(),
                entity.getOrderDetailId(),
                entity.getReturnedQuantity(),
                entity.getNotes()));
        return entity;
    }

    @Override
    public boolean update(ProductReturnDetail entity) throws SQLException {
        String sql = "UPDATE product_return_detail SET returned_quantity = ?, notes = ? WHERE id = ?";

        return executeUpdate(sql,
                entity.getReturnedQuantity(),
                entity.getNotes(),
                entity.getId());
    }

    @Override
    public List<ProductReturnDetail> findByReturnId(Long returnId) throws SQLException {
        return queryList("SELECT * FROM product_return_detail WHERE return_id = ? ORDER BY id", returnId);
    }

    @Override
    public List<ProductReturnDetail> findByOrderDetailId(Long orderDetailId) throws SQLException {
        return queryList("SELECT * FROM product_return_detail WHERE order_detail_id = ? ORDER BY id",
                orderDetailId);
    }
}
