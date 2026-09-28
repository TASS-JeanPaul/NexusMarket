package application.services;

import application.domain.enums.InvoiceStatus;
import application.domain.exceptions.ResourceNotFoundException;
import application.domain.interfaces.IInvoiceRepository;
import application.domain.models.Invoice;
import application.domain.models.Order;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InvoiceService extends BaseService {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.19");
    private static final int AMOUNT_SCALE = 2;

    private final IInvoiceRepository invoiceRepository;

    public InvoiceService(IInvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public Invoice issueFor(Order order) {
        return execute(() -> {
            require(order != null, "The order is required");
            requireIdentifier(order.getId(), "The order identifier");
            requirePositiveAmount(order.getTotal(), "The order total");
            require(invoiceRepository.findByOrderId(order.getId()).isEmpty(),
                    "The order " + order.getId() + " already has an invoice issued");

            BigDecimal subtotal = order.getTotal().setScale(AMOUNT_SCALE, RoundingMode.HALF_UP);
            BigDecimal taxes = subtotal.multiply(TAX_RATE).setScale(AMOUNT_SCALE, RoundingMode.HALF_UP);

            Invoice invoice = new Invoice();
            invoice.setInvoiceNumber(generateDocumentNumber("INV"));
            invoice.setOrderId(order.getId());
            invoice.setSubtotal(subtotal);
            invoice.setTaxes(taxes);
            invoice.setTotal(subtotal.add(taxes));
            invoice.setStatus(InvoiceStatus.ISSUED);
            invoice.setIssuedAt(LocalDateTime.now());

            return invoiceRepository.save(invoice);
        });
    }

    public Invoice findById(Long id) {
        requireIdentifier(id, "The invoice identifier");
        return execute(() -> findOrFail(invoiceRepository.findById(id), "Invoice", id));
    }

    public List<Invoice> findAll() {
        return execute(invoiceRepository::findAll);
    }

    public Invoice findByOrder(Long orderId) {
        requireIdentifier(orderId, "The order identifier");
        return execute(() -> invoiceRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "The order " + orderId + " has no invoice issued")));
    }

    public Invoice findByNumber(String invoiceNumber) {
        requireText(invoiceNumber, "The invoice number");
        return execute(() -> invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No invoice is registered with the number " + invoiceNumber)));
    }

    public Invoice markAsPaid(Long invoiceId) {
        return execute(() -> {
            Invoice invoice = findById(invoiceId);
            require(invoice.getStatus() == InvoiceStatus.ISSUED,
                    "Only an issued invoice can be marked as paid, and the invoice " + invoiceId
                            + " is " + invoice.getStatus());

            invoice.setStatus(InvoiceStatus.PAID);
            invoiceRepository.update(invoice);
            return invoice;
        });
    }

    public Invoice annul(Long invoiceId) {
        return execute(() -> {
            Invoice invoice = findById(invoiceId);
            require(invoice.getStatus() != InvoiceStatus.PAID,
                    "A paid invoice cannot be voided; it requires a refund instead");
            require(invoice.getStatus() != InvoiceStatus.VOIDED,
                    "The invoice " + invoiceId + " is already voided");

            invoice.setStatus(InvoiceStatus.VOIDED);
            invoiceRepository.update(invoice);
            return invoice;
        });
    }
}
