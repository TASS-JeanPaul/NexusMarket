package application.data.contracts;

import application.data.entities.Warehouse;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IWarehouseRepository extends IRepository<Warehouse> {
    Optional<Warehouse> findByCode(String code) throws SQLException;

    List<Warehouse> findBySellerId(Long sellerId) throws SQLException;

    List<Warehouse> findActive() throws SQLException;
}
