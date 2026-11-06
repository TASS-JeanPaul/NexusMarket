package application.dialog.controllers;

import application.data.entities.Invoice;
import application.models.services.InvoiceService;

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

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public List<Invoice> findAll() {
        return invoiceService.findAll();
    }

    @GetMapping("/{id}")
    public Invoice findById(@PathVariable Long id) {
        return invoiceService.findById(id);
    }

    @GetMapping("/by-order")
    public Invoice findByOrder(@RequestParam Long orderId) {
        return invoiceService.findByOrder(orderId);
    }

    @GetMapping("/by-number/{invoiceNumber}")
    public Invoice findByNumber(@PathVariable String invoiceNumber) {
        return invoiceService.findByNumber(invoiceNumber);
    }

    @PutMapping("/{id}/payment")
    public Invoice markAsPaid(@PathVariable Long id) {
        return invoiceService.markAsPaid(id);
    }

    @PutMapping("/{id}/annulment")
    public Invoice annul(@PathVariable Long id) {
        return invoiceService.annul(id);
    }
}
