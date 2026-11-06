package application.dialog.controllers;

import application.data.entities.Invoice;
import application.models.ports.in.IInvoiceUseCase;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "09 Facturas", description = "Facturacion de las compras, con numeracion propia")
@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final IInvoiceUseCase invoiceUseCase;

    public InvoiceController(IInvoiceUseCase invoiceUseCase) {
        this.invoiceUseCase = invoiceUseCase;
    }

    @GetMapping
    public List<Invoice> findAll() {
        return invoiceUseCase.findAll();
    }

    @GetMapping("/{id}")
    public Invoice findById(@PathVariable Long id) {
        return invoiceUseCase.findById(id);
    }

    @GetMapping("/by-order")
    public Invoice findByOrder(@RequestParam Long orderId) {
        return invoiceUseCase.findByOrder(orderId);
    }

    @GetMapping("/by-number/{invoiceNumber}")
    public Invoice findByNumber(@PathVariable String invoiceNumber) {
        return invoiceUseCase.findByNumber(invoiceNumber);
    }

    @PutMapping("/{id}/payment")
    public Invoice markAsPaid(@PathVariable Long id) {
        return invoiceUseCase.markAsPaid(id);
    }

    @PutMapping("/{id}/annulment")
    public Invoice annul(@PathVariable Long id) {
        return invoiceUseCase.annul(id);
    }
}
