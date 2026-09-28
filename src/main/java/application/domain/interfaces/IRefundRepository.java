package application.domain.interfaces;

import application.domain.models.Refund;

import java.sql.SQLException;
import java.util.Optional;

public interface IRefundRepository extends IRepository<Refund> {
    Optional<Refund> findByReturnId(Long returnId) throws SQLException;
}
