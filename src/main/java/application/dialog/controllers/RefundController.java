package application.dialog.controllers;

import application.data.entities.Refund;
import application.dialog.requests.RefundRequest;
import application.models.services.RefundService;

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

@RestController
@RequestMapping("/api/refunds")
public class RefundController {

    private final RefundService refundService;

    public RefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Refund request(@RequestBody RefundRequest request) {
        return refundService.request(request.returnId(), request.amount(), request.method());
    }

    @GetMapping
    public List<Refund> findAll() {
        return refundService.findAll();
    }

    @GetMapping("/{id}")
    public Refund findById(@PathVariable Long id) {
        return refundService.findById(id);
    }

    @GetMapping("/by-return")
    public Refund findByReturn(@RequestParam Long returnId) {
        return refundService.findByReturn(returnId);
    }

    @PutMapping("/{id}/approval")
    public Refund approve(@PathVariable Long id, @RequestParam Long authorizerId) {
        return refundService.approve(id, authorizerId);
    }

    @PutMapping("/{id}/rejection")
    public Refund reject(@PathVariable Long id, @RequestParam Long authorizerId) {
        return refundService.reject(id, authorizerId);
    }

    @PutMapping("/{id}/payment")
    public Refund markAsPaid(@PathVariable Long id) {
        return refundService.markAsPaid(id);
    }
}
