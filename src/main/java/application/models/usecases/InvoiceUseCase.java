package application.models.usecases;

import application.models.ports.in.IInvoiceUseCase;
import application.models.services.InvoiceService;
import application.data.enums.InvoiceStatus;
import application.shared.exceptions.ResourceNotFoundException;
import application.data.ports.out.IInvoiceRepository;
import application.data.entities.Invoice;
import application.data.entities.Order;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class InvoiceUseCase implements IInvoiceUseCase {

    private final InvoiceService invoiceService;

    public InvoiceUseCase(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Override
    public Invoice issueFor(Order order) {
        return invoiceService.issueFor(order);
    }

    @Override
    public Invoice findById(Long id) {
        return invoiceService.findById(id);
    }

    @Override
    public List<Invoice> findAll() {
        return invoiceService.findAll();
    }

    @Override
    public Invoice findByOrder(Long orderId) {
        return invoiceService.findByOrder(orderId);
    }

    @Override
    public Invoice findByNumber(String invoiceNumber) {
        return invoiceService.findByNumber(invoiceNumber);
    }

    @Override
    public Invoice markAsPaid(Long invoiceId) {
        return invoiceService.markAsPaid(invoiceId);
    }

    @Override
    public Invoice annul(Long invoiceId) {
        return invoiceService.annul(invoiceId);
    }
}
