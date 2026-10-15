package application.data.repositories;

import application.data.contracts.IInventoryMovementRepository;
import application.data.entities.InventoryMovement;
import application.data.enums.MovementType;

import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class InventoryMovementRepository extends BaseRepository<InventoryMovement>
        implements IInventoryMovementRepository {

    @Override
    protected String getTableName() {
        return "inventory_movement";
    }

    @Override
    protected InventoryMovement mapRow(ResultSet row) throws SQLException {
        InventoryMovement movement = new InventoryMovement();
        movement.setId(row.getLong("id"));
        movement.setInventoryId(toIdentifier(row, "inventory_id"));
        movement.setType(MovementType.valueOf(row.getString("type")));
        movement.setQuantity(row.getInt("quantity"));
        movement.setUserId(toIdentifier(row, "user_id"));
        movement.setNotes(row.getString("notes"));
        movement.setDate(toDateTime(row.getTimestamp("date")));
        return movement;
    }

    @Override
    public InventoryMovement save(InventoryMovement entity) throws SQLException {
        String sql = "INSERT INTO inventory_movement (inventory_id, type, quantity, user_id, notes, date) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        entity.setId(insertAndGetId(sql,
                entity.getInventoryId(),
                entity.getType().name(),
                entity.getQuantity(),
                entity.getUserId(),
                entity.getNotes(),
                entity.getDate()));
        return entity;
    }

    @Override
    public boolean update(InventoryMovement entity) throws SQLException {
        return executeUpdate("UPDATE inventory_movement SET notes = ? WHERE id = ?",
                entity.getNotes(),
                entity.getId());
    }

    @Override
    public List<InventoryMovement> findByInventoryId(Long inventoryId) throws SQLException {
        return queryList("SELECT * FROM inventory_movement WHERE inventory_id = ? ORDER BY date DESC, id DESC",
                inventoryId);
    }
}
