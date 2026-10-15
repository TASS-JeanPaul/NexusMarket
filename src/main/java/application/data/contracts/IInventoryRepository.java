package application.data.contracts;

import application.data.entities.Inventory;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IInventoryRepository extends IRepository<Inventory> {
    Optional<Inventory> findByProductVariantAndWarehouse(Long productId, Long variantId, Long warehouseId)
            throws SQLException;

    List<Inventory> findByProductId(Long productId) throws SQLException;

    List<Inventory> findByWarehouseId(Long warehouseId) throws SQLException;
}
