package application.data.repositories;

import application.data.contracts.IWarehouseRepository;
import application.data.entities.Warehouse;
import application.data.enums.WarehouseType;

import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class WarehouseRepository extends BaseRepository<Warehouse> implements IWarehouseRepository {

    @Override
    protected String getTableName() {
        return "warehouse";
    }

    @Override
    protected Warehouse mapRow(ResultSet row) throws SQLException {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(row.getLong("id"));
        warehouse.setCode(row.getString("code"));
        warehouse.setName(row.getString("name"));
        warehouse.setStreet(row.getString("street"));
        warehouse.setCity(row.getString("city"));
        warehouse.setType(WarehouseType.valueOf(row.getString("type")));
        warehouse.setSellerId(toIdentifier(row, "seller_id"));
        warehouse.setActive(row.getBoolean("active"));
        return warehouse;
    }

    @Override
    public Warehouse save(Warehouse entity) throws SQLException {
        String sql = "INSERT INTO warehouse (code, name, street, city, type, seller_id, active) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        entity.setId(insertAndGetId(sql,
                entity.getCode(),
                entity.getName(),
                entity.getStreet(),
                entity.getCity(),
                entity.getType().name(),
                entity.getSellerId(),
                entity.isActive()));
        return entity;
    }

    @Override
    public boolean update(Warehouse entity) throws SQLException {
        String sql = "UPDATE warehouse SET code = ?, name = ?, street = ?, city = ?, type = ?, "
                + "seller_id = ?, active = ? WHERE id = ?";

        return executeUpdate(sql,
                entity.getCode(),
                entity.getName(),
                entity.getStreet(),
                entity.getCity(),
                entity.getType().name(),
                entity.getSellerId(),
                entity.isActive(),
                entity.getId());
    }

    @Override
    public Optional<Warehouse> findByCode(String code) throws SQLException {
        return queryOne("SELECT * FROM warehouse WHERE code = ?", code);
    }

    @Override
    public List<Warehouse> findBySellerId(Long sellerId) throws SQLException {
        return queryList("SELECT * FROM warehouse WHERE seller_id = ? ORDER BY name", sellerId);
    }

    @Override
    public List<Warehouse> findActive() throws SQLException {
        return queryList("SELECT * FROM warehouse WHERE active = TRUE ORDER BY name");
    }
}
