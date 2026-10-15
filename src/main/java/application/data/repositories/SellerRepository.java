package application.data.repositories;

import application.data.contracts.ISellerRepository;
import application.data.entities.Seller;
import application.data.enums.UserStatus;

import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class SellerRepository extends BaseRepository<Seller> implements ISellerRepository {

    private static final String SELECT_SELLER =
            "SELECT u.id, u.full_name, u.identity_document, u.email, u.phone, u.role, u.status, "
                    + "u.registered_at, s.business_name, s.tax_id, s.registered_by_id "
                    + "FROM seller s JOIN app_user u ON u.id = s.id";

    @Override
    protected String getTableName() {
        return "seller";
    }

    @Override
    protected Seller mapRow(ResultSet row) throws SQLException {
        Seller seller = new Seller();
        seller.setId(row.getLong("id"));
        seller.setFullName(row.getString("full_name"));
        seller.setIdentityDocument(row.getString("identity_document"));
        seller.setEmail(row.getString("email"));
        seller.setPhone(row.getString("phone"));
        seller.setStatus(UserStatus.valueOf(row.getString("status")));
        seller.setRegisteredAt(toDateTime(row.getTimestamp("registered_at")));
        seller.setBusinessName(row.getString("business_name"));
        seller.setTaxId(row.getString("tax_id"));
        seller.setRegisteredById(toIdentifier(row, "registered_by_id"));
        return seller;
    }

    @Override
    public List<Seller> findAll() throws SQLException {
        return queryList(SELECT_SELLER + " ORDER BY s.business_name");
    }

    @Override
    public Optional<Seller> findById(Long id) throws SQLException {
        return queryOne(SELECT_SELLER + " WHERE s.id = ?", id);
    }

    @Override
    public Optional<Seller> findByTaxId(String taxId) throws SQLException {
        return queryOne(SELECT_SELLER + " WHERE s.tax_id = ?", taxId);
    }

    @Override
    public Seller save(Seller entity) throws SQLException {
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

        executeUpdate("INSERT INTO seller (id, business_name, tax_id, registered_by_id) VALUES (?, ?, ?, ?)",
                id,
                entity.getBusinessName(),
                entity.getTaxId(),
                entity.getRegisteredById());

        entity.setId(id);
        return entity;
    }

    @Override
    public boolean update(Seller entity) throws SQLException {
        String userSql = "UPDATE app_user SET full_name = ?, identity_document = ?, email = ?, phone = ?, "
                + "status = ? WHERE id = ?";

        boolean updated = executeUpdate(userSql,
                entity.getFullName(),
                entity.getIdentityDocument(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getStatus().name(),
                entity.getId());

        executeUpdate("UPDATE seller SET business_name = ?, tax_id = ? WHERE id = ?",
                entity.getBusinessName(),
                entity.getTaxId(),
                entity.getId());

        return updated;
    }

    @Override
    public boolean delete(Long id) throws SQLException {
        executeUpdate("DELETE FROM seller WHERE id = ?", id);
        return executeUpdate("DELETE FROM app_user WHERE id = ?", id);
    }
}
