package application.data.repositories;

import application.data.contracts.IRefundRepository;
import application.data.entities.Refund;
import application.data.enums.RefundMethod;
import application.data.enums.RefundStatus;

import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class RefundRepository extends BaseRepository<Refund> implements IRefundRepository {

    @Override
    protected String getTableName() {
        return "refund";
    }

    @Override
    protected Refund mapRow(ResultSet row) throws SQLException {
        Refund refund = new Refund();
        refund.setId(row.getLong("id"));
        refund.setReturnId(toIdentifier(row, "return_id"));
        refund.setAmount(row.getBigDecimal("amount"));
        refund.setMethod(RefundMethod.valueOf(row.getString("method")));
        refund.setStatus(RefundStatus.valueOf(row.getString("status")));
        refund.setAuthorizedById(toIdentifier(row, "authorized_by_id"));
        refund.setRequestedAt(toDateTime(row.getTimestamp("requested_at")));
        refund.setPaidAt(toDateTime(row.getTimestamp("paid_at")));
        return refund;
    }

    @Override
    public Refund save(Refund entity) throws SQLException {
        String sql = "INSERT INTO refund "
                + "(return_id, amount, method, status, authorized_by_id, requested_at, paid_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        entity.setId(insertAndGetId(sql,
                entity.getReturnId(),
                entity.getAmount(),
                entity.getMethod().name(),
                entity.getStatus().name(),
                entity.getAuthorizedById(),
                entity.getRequestedAt(),
                entity.getPaidAt()));
        return entity;
    }

    @Override
    public boolean update(Refund entity) throws SQLException {
        String sql = "UPDATE refund SET status = ?, authorized_by_id = ?, paid_at = ? WHERE id = ?";

        return executeUpdate(sql,
                entity.getStatus().name(),
                entity.getAuthorizedById(),
                entity.getPaidAt(),
                entity.getId());
    }

    @Override
    public Optional<Refund> findByReturnId(Long returnId) throws SQLException {
        return queryOne("SELECT * FROM refund WHERE return_id = ?", returnId);
    }
}
