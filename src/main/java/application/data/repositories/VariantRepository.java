package application.data.repositories;

import application.data.contracts.IVariantRepository;
import application.data.entities.Variant;

import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class VariantRepository extends BaseRepository<Variant> implements IVariantRepository {

    @Override
    protected String getTableName() {
        return "variant";
    }

    @Override
    protected Variant mapRow(ResultSet row) throws SQLException {
        Variant variant = new Variant();
        variant.setId(row.getLong("id"));
        variant.setProductId(toIdentifier(row, "product_id"));
        variant.setSku(row.getString("sku"));
        variant.setName(row.getString("name"));
        variant.setValue(row.getString("value"));
        variant.setAdditionalPrice(row.getBigDecimal("additional_price"));
        return variant;
    }

    @Override
    public Variant save(Variant entity) throws SQLException {
        String sql = "INSERT INTO variant (product_id, sku, name, value, additional_price) "
                + "VALUES (?, ?, ?, ?, ?)";

        entity.setId(insertAndGetId(sql,
                entity.getProductId(),
                entity.getSku(),
                entity.getName(),
                entity.getValue(),
                entity.getAdditionalPrice()));
        return entity;
    }

    @Override
    public boolean update(Variant entity) throws SQLException {
        String sql = "UPDATE variant SET sku = ?, name = ?, value = ?, additional_price = ? WHERE id = ?";

        return executeUpdate(sql,
                entity.getSku(),
                entity.getName(),
                entity.getValue(),
                entity.getAdditionalPrice(),
                entity.getId());
    }

    @Override
    public List<Variant> findByProductId(Long productId) throws SQLException {
        return queryList("SELECT * FROM variant WHERE product_id = ? ORDER BY name, value", productId);
    }

    @Override
    public Optional<Variant> findBySku(String sku) throws SQLException {
        return queryOne("SELECT * FROM variant WHERE sku = ?", sku);
    }
}
