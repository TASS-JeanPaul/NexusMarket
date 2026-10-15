package application.data.repositories;

import application.data.contracts.IInventoryRepository;
import application.data.entities.Inventory;
import application.data.enums.InventoryStatus;

import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class InventoryRepository extends BaseRepository<Inventory> implements IInventoryRepository {

    @Override
    protected String getTableName() {
        return "inventory";
    }

    @Override
    protected Inventory mapRow(ResultSet row) throws SQLException {
        Inventory inventory = new Inventory();
        inventory.setId(row.getLong("id"));
        inventory.setProductId(toIdentifier(row, "product_id"));
        inventory.setVariantId(toIdentifier(row, "variant_id"));
        inventory.setWarehouseId(toIdentifier(row, "warehouse_id"));
        inventory.setAvailableQuantity(row.getInt("available_quantity"));
        inventory.setReservedQuantity(row.getInt("reserved_quantity"));
        inventory.setStatus(InventoryStatus.valueOf(row.getString("status")));
        inventory.setUpdatedAt(toDateTime(row.getTimestamp("updated_at")));
        return inventory;
    }

    @Override
    public Inventory save(Inventory entity) throws SQLException {
        String sql = "INSERT INTO inventory "
                + "(product_id, variant_id, warehouse_id, available_quantity, reserved_quantity, status, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        entity.setId(insertAndGetId(sql,
                entity.getProductId(),
                entity.getVariantId(),
                entity.getWarehouseId(),
                entity.getAvailableQuantity(),
                entity.getReservedQuantity(),
                entity.getStatus().name(),
                entity.getUpdatedAt()));
        return entity;
    }

    @Override
    public boolean update(Inventory entity) throws SQLException {
        String sql = "UPDATE inventory SET available_quantity = ?, reserved_quantity = ?, status = ?, "
                + "updated_at = ? WHERE id = ?";

        return executeUpdate(sql,
                entity.getAvailableQuantity(),
                entity.getReservedQuantity(),
                entity.getStatus().name(),
                entity.getUpdatedAt(),
                entity.getId());
    }

    @Override
    public Optional<Inventory> findByProductVariantAndWarehouse(Long productId, Long variantId, Long warehouseId)
            throws SQLException {
        String sql = "SELECT * FROM inventory WHERE product_id = ? "
                + "AND variant_id IS NOT DISTINCT FROM ? AND warehouse_id = ?";

        return queryOne(sql, productId, variantId, warehouseId);
    }

    @Override
    public List<Inventory> findByProductId(Long productId) throws SQLException {
        return queryList("SELECT * FROM inventory WHERE product_id = ? ORDER BY warehouse_id", productId);
    }

    @Override
    public List<Inventory> findByWarehouseId(Long warehouseId) throws SQLException {
        return queryList("SELECT * FROM inventory WHERE warehouse_id = ? ORDER BY product_id", warehouseId);
    }
}
