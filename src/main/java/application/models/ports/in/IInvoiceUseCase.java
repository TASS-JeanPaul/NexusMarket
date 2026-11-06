package application.models.ports.in;

import application.data.enums.InvoiceStatus;
import application.shared.exceptions.ResourceNotFoundException;
import application.data.ports.out.IInvoiceRepository;
import application.data.entities.Invoice;
import application.data.entities.Order;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

public interface IInvoiceUseCase {

    Invoice issueFor(Order order);

    Invoice findById(Long id);

    List<Invoice> findAll();

    Invoice findByOrder(Long orderId);

    Invoice findByNumber(String invoiceNumber);

    Invoice markAsPaid(Long invoiceId);

    Invoice annul(Long invoiceId);
}
