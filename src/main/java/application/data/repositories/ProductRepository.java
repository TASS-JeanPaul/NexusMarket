package application.data.repositories;

import application.data.contracts.IProductRepository;
import application.data.entities.DigitalProduct;
import application.data.entities.PhysicalProduct;
import application.data.entities.Product;
import application.data.enums.ProductStatus;
import application.data.enums.ProductType;

import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository extends BaseRepository<Product> implements IProductRepository {

    @Override
    protected String getTableName() {
        return "product";
    }

    @Override
    protected Product mapRow(ResultSet row) throws SQLException {
        Product product = instantiate(ProductType.valueOf(row.getString("type")));
        product.setId(row.getLong("id"));
        product.setCode(row.getString("code"));
        product.setName(row.getString("name"));
        product.setDescription(row.getString("description"));
        product.setPrice(row.getBigDecimal("price"));
        product.setStatus(ProductStatus.valueOf(row.getString("status")));
        product.setSellerId(toIdentifier(row, "seller_id"));
        product.setCategoryId(toIdentifier(row, "category_id"));
        product.setCreatedAt(toDateTime(row.getTimestamp("created_at")));
        return product;
    }

    @Override
    public Product save(Product entity) throws SQLException {
        String sql = "INSERT INTO product "
                + "(code, name, description, price, type, status, seller_id, category_id, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        entity.setId(insertAndGetId(sql,
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getType().name(),
                entity.getStatus().name(),
                entity.getSellerId(),
                entity.getCategoryId(),
                entity.getCreatedAt()));
        return entity;
    }

    @Override
    public boolean update(Product entity) throws SQLException {
        String sql = "UPDATE product SET code = ?, name = ?, description = ?, price = ?, status = ?, "
                + "category_id = ? WHERE id = ?";

        return executeUpdate(sql,
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getStatus().name(),
                entity.getCategoryId(),
                entity.getId());
    }

    @Override
    public Optional<Product> findByCode(String code) throws SQLException {
        return queryOne("SELECT * FROM product WHERE code = ?", code);
    }

    @Override
    public List<Product> findBySellerId(Long sellerId) throws SQLException {
        return queryList("SELECT * FROM product WHERE seller_id = ? ORDER BY name", sellerId);
    }

    @Override
    public List<Product> findByCategoryId(Long categoryId) throws SQLException {
        return queryList("SELECT * FROM product WHERE category_id = ? ORDER BY name", categoryId);
    }

    @Override
    public List<Product> findByStatus(ProductStatus status) throws SQLException {
        return queryList("SELECT * FROM product WHERE status = ? ORDER BY name", status.name());
    }

    private Product instantiate(ProductType type) {
        return type == ProductType.PHYSICAL ? new PhysicalProduct() : new DigitalProduct();
    }
}
