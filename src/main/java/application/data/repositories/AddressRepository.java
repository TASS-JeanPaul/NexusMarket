package application.data.repositories;

import application.data.contracts.IAddressRepository;
import application.data.entities.Address;

import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class AddressRepository extends BaseRepository<Address> implements IAddressRepository {

    @Override
    protected String getTableName() {
        return "address";
    }

    @Override
    protected Address mapRow(ResultSet row) throws SQLException {
        Address address = new Address();
        address.setId(row.getLong("id"));
        address.setBuyerId(toIdentifier(row, "buyer_id"));
        address.setStreet(row.getString("street"));
        address.setCity(row.getString("city"));
        address.setState(row.getString("state"));
        address.setNotes(row.getString("notes"));
        address.setPrimary(row.getBoolean("is_primary"));
        return address;
    }

    @Override
    public Address save(Address entity) throws SQLException {
        String sql = "INSERT INTO address (buyer_id, street, city, state, notes, is_primary) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        entity.setId(insertAndGetId(sql,
                entity.getBuyerId(),
                entity.getStreet(),
                entity.getCity(),
                entity.getState(),
                entity.getNotes(),
                entity.isPrimary()));
        return entity;
    }

    @Override
    public boolean update(Address entity) throws SQLException {
        String sql = "UPDATE address SET street = ?, city = ?, state = ?, notes = ?, is_primary = ? "
                + "WHERE id = ?";

        return executeUpdate(sql,
                entity.getStreet(),
                entity.getCity(),
                entity.getState(),
                entity.getNotes(),
                entity.isPrimary(),
                entity.getId());
    }

    @Override
    public List<Address> findByBuyerId(Long buyerId) throws SQLException {
        return queryList("SELECT * FROM address WHERE buyer_id = ? ORDER BY is_primary DESC, id", buyerId);
    }

    @Override
    public Optional<Address> findPrimaryByBuyerId(Long buyerId) throws SQLException {
        return queryOne("SELECT * FROM address WHERE buyer_id = ? AND is_primary = TRUE", buyerId);
    }
}
