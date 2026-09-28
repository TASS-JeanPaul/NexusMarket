package application.domain.interfaces;

import application.domain.models.Invoice;

import java.sql.SQLException;
import java.util.Optional;

public interface IInvoiceRepository extends IRepository<Invoice> {
    Optional<Invoice> findByOrderId(Long orderId) throws SQLException;

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber) throws SQLException;
}
