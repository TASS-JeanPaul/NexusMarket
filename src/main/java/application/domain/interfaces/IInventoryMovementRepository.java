package application.domain.interfaces;

import application.domain.models.InventoryMovement;

import java.sql.SQLException;
import java.util.List;

public interface IInventoryMovementRepository extends IRepository<InventoryMovement> {
    List<InventoryMovement> findByInventoryId(Long inventoryId) throws SQLException;
}
