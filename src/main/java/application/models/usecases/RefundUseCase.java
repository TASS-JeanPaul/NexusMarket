package application.models.usecases;

import application.models.ports.in.IRefundUseCase;
import application.models.services.RefundService;
import application.data.enums.RefundMethod;
import application.data.enums.RefundStatus;
import application.data.enums.Role;
import application.shared.exceptions.ResourceNotFoundException;
import application.data.ports.out.IRefundRepository;
import application.data.entities.Refund;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class RefundUseCase implements IRefundUseCase {

    private final RefundService refundService;

    public RefundUseCase(RefundService refundService) {
        this.refundService = refundService;
    }

    @Override
    public Refund request(Long returnId, BigDecimal amount, RefundMethod method) {
        return refundService.request(returnId, amount, method);
    }

    @Override
    public Refund findById(Long id) {
        return refundService.findById(id);
    }

    @Override
    public List<Refund> findAll() {
        return refundService.findAll();
    }

    @Override
    public Refund findByReturn(Long returnId) {
        return refundService.findByReturn(returnId);
    }

    @Override
    public Refund approve(Long refundId, Long authorizerId) {
        return refundService.approve(refundId, authorizerId);
    }

    @Override
    public Refund reject(Long refundId, Long authorizerId) {
        return refundService.reject(refundId, authorizerId);
    }

    @Override
    public Refund markAsPaid(Long refundId) {
        return refundService.markAsPaid(refundId);
    }
}
