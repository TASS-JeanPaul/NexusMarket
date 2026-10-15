package application.data.contracts;

import application.data.entities.Variant;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IVariantRepository extends IRepository<Variant> {
    List<Variant> findByProductId(Long productId) throws SQLException;

    Optional<Variant> findBySku(String sku) throws SQLException;
}
