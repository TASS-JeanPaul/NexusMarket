package application.dialog.controllers;

import application.data.entities.Refund;
import application.dialog.requests.RefundRequest;
import application.models.ports.in.IRefundUseCase;

import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "12 Reembolsos", description = "Devolucion del dinero, posterior a la devolucion de la mercancia")
@RestController
@RequestMapping("/api/refunds")
public class RefundController {

    private final IRefundUseCase refundUseCase;

    public RefundController(IRefundUseCase refundUseCase) {
        this.refundUseCase = refundUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Refund request(@Valid @RequestBody RefundRequest request) {
        return refundUseCase.request(request.returnId(), request.amount(), request.method());
    }

    @GetMapping
    public List<Refund> findAll() {
        return refundUseCase.findAll();
    }

    @GetMapping("/{id}")
    public Refund findById(@PathVariable Long id) {
        return refundUseCase.findById(id);
    }

    @GetMapping("/by-return")
    public Refund findByReturn(@RequestParam Long returnId) {
        return refundUseCase.findByReturn(returnId);
    }

    @PutMapping("/{id}/approval")
    public Refund approve(@PathVariable Long id, @RequestParam Long authorizerId) {
        return refundUseCase.approve(id, authorizerId);
    }

    @PutMapping("/{id}/rejection")
    public Refund reject(@PathVariable Long id, @RequestParam Long authorizerId) {
        return refundUseCase.reject(id, authorizerId);
    }

    @PutMapping("/{id}/payment")
    public Refund markAsPaid(@PathVariable Long id) {
        return refundUseCase.markAsPaid(id);
    }
}
