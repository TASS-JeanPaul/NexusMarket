package application.data.repositories;

import application.data.contracts.IBuyerRepository;
import application.data.entities.Buyer;
import application.data.enums.CommercialStatus;
import application.data.enums.UserStatus;

import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class BuyerRepository extends BaseRepository<Buyer> implements IBuyerRepository {

    private static final String SELECT_BUYER =
            "SELECT u.id, u.full_name, u.identity_document, u.email, u.phone, u.role, u.status, "
                    + "u.registered_at, b.commercial_status "
                    + "FROM buyer b JOIN app_user u ON u.id = b.id";

    @Override
    protected String getTableName() {
        return "buyer";
    }

    @Override
    protected Buyer mapRow(ResultSet row) throws SQLException {
        Buyer buyer = new Buyer();
        buyer.setId(row.getLong("id"));
        buyer.setFullName(row.getString("full_name"));
        buyer.setIdentityDocument(row.getString("identity_document"));
        buyer.setEmail(row.getString("email"));
        buyer.setPhone(row.getString("phone"));
        buyer.setStatus(UserStatus.valueOf(row.getString("status")));
        buyer.setRegisteredAt(toDateTime(row.getTimestamp("registered_at")));
        buyer.setCommercialStatus(CommercialStatus.valueOf(row.getString("commercial_status")));
        return buyer;
    }

    @Override
    public List<Buyer> findAll() throws SQLException {
        return queryList(SELECT_BUYER + " ORDER BY u.full_name");
    }

    @Override
    public Optional<Buyer> findById(Long id) throws SQLException {
        return queryOne(SELECT_BUYER + " WHERE b.id = ?", id);
    }

    @Override
    public Optional<Buyer> findByEmail(String email) throws SQLException {
        return queryOne(SELECT_BUYER + " WHERE u.email = ?", email);
    }

    @Override
    public Buyer save(Buyer entity) throws SQLException {
        String userSql = "INSERT INTO app_user "
                + "(full_name, identity_document, email, phone, role, status, registered_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Long id = insertAndGetId(userSql,
                entity.getFullName(),
                entity.getIdentityDocument(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getRole().name(),
                entity.getStatus().name(),
                entity.getRegisteredAt());

        executeUpdate("INSERT INTO buyer (id, commercial_status) VALUES (?, ?)",
                id,
                entity.getCommercialStatus().name());

        entity.setId(id);
        return entity;
    }

    @Override
    public boolean update(Buyer entity) throws SQLException {
        String userSql = "UPDATE app_user SET full_name = ?, identity_document = ?, email = ?, phone = ?, "
                + "status = ? WHERE id = ?";

        boolean updated = executeUpdate(userSql,
                entity.getFullName(),
                entity.getIdentityDocument(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getStatus().name(),
                entity.getId());

        executeUpdate("UPDATE buyer SET commercial_status = ? WHERE id = ?",
                entity.getCommercialStatus().name(),
                entity.getId());

        return updated;
    }

    @Override
    public boolean delete(Long id) throws SQLException {
        executeUpdate("DELETE FROM buyer WHERE id = ?", id);
        return executeUpdate("DELETE FROM app_user WHERE id = ?", id);
    }
}
