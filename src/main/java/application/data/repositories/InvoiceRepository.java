package application.data.repositories;

import application.data.contracts.IInvoiceRepository;
import application.data.entities.Invoice;
import application.data.enums.InvoiceStatus;

import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class InvoiceRepository extends BaseRepository<Invoice> implements IInvoiceRepository {

    @Override
    protected String getTableName() {
        return "invoice";
    }

    @Override
    protected Invoice mapRow(ResultSet row) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setId(row.getLong("id"));
        invoice.setInvoiceNumber(row.getString("invoice_number"));
        invoice.setOrderId(toIdentifier(row, "order_id"));
        invoice.setSubtotal(row.getBigDecimal("subtotal"));
        invoice.setTaxes(row.getBigDecimal("taxes"));
        invoice.setTotal(row.getBigDecimal("total"));
        invoice.setStatus(InvoiceStatus.valueOf(row.getString("status")));
        invoice.setIssuedAt(toDateTime(row.getTimestamp("issued_at")));
        return invoice;
    }

    @Override
    public Invoice save(Invoice entity) throws SQLException {
        String sql = "INSERT INTO invoice "
                + "(invoice_number, order_id, subtotal, taxes, total, status, issued_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        entity.setId(insertAndGetId(sql,
                entity.getInvoiceNumber(),
                entity.getOrderId(),
                entity.getSubtotal(),
                entity.getTaxes(),
                entity.getTotal(),
                entity.getStatus().name(),
                entity.getIssuedAt()));
        return entity;
    }

    @Override
    public boolean update(Invoice entity) throws SQLException {
        return executeUpdate("UPDATE invoice SET status = ? WHERE id = ?",
                entity.getStatus().name(),
                entity.getId());
    }

    @Override
    public Optional<Invoice> findByOrderId(Long orderId) throws SQLException {
        return queryOne("SELECT * FROM invoice WHERE order_id = ?", orderId);
    }

    @Override
    public Optional<Invoice> findByInvoiceNumber(String invoiceNumber) throws SQLException {
        return queryOne("SELECT * FROM invoice WHERE invoice_number = ?", invoiceNumber);
    }
}
