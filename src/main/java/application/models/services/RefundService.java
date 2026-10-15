package application.models.services;

import application.data.enums.RefundMethod;
import application.data.enums.RefundStatus;
import application.data.enums.Role;
import application.shared.exceptions.ResourceNotFoundException;
import application.data.contracts.IRefundRepository;
import application.data.entities.Refund;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class RefundService extends BaseService {

    private static final Map<RefundStatus, Set<RefundStatus>> ALLOWED_TRANSITIONS = Map.of(
            RefundStatus.PENDING, EnumSet.of(RefundStatus.APPROVED, RefundStatus.REJECTED),
            RefundStatus.APPROVED, EnumSet.of(RefundStatus.PAID),
            RefundStatus.PAID, EnumSet.noneOf(RefundStatus.class),
            RefundStatus.REJECTED, EnumSet.noneOf(RefundStatus.class));

    private final IRefundRepository refundRepository;
    private final ReturnService returnService;
    private final UserService userService;

    public RefundService(IRefundRepository refundRepository,
                         ReturnService returnService,
                         UserService userService) {
        this.refundRepository = refundRepository;
        this.returnService = returnService;
        this.userService = userService;
    }

    public Refund request(Long returnId, BigDecimal amount, RefundMethod method) {
        return execute(() -> {
            returnService.requireRefundable(returnId);
            requirePositiveAmount(amount, "The refund amount");
            require(method != null, "The refund method is required");
            require(refundRepository.findByReturnId(returnId).isEmpty(),
                    "The return " + returnId + " already has a refund registered");

            BigDecimal returnedValue = returnService.returnedValue(returnId);
            require(amount.compareTo(returnedValue) <= 0,
                    "The refund amount cannot exceed the value of the returned products, which is "
                            + returnedValue);

            Refund refund = new Refund();
            refund.setReturnId(returnId);
            refund.setAmount(amount);
            refund.setMethod(method);
            refund.setStatus(RefundStatus.PENDING);
            refund.setRequestedAt(LocalDateTime.now());

            return refundRepository.save(refund);
        });
    }

    public Refund findById(Long id) {
        requireIdentifier(id, "The refund identifier");
        return execute(() -> findOrFail(refundRepository.findById(id), "Refund", id));
    }

    public List<Refund> findAll() {
        return execute(refundRepository::findAll);
    }

    public Refund findByReturn(Long returnId) {
        requireIdentifier(returnId, "The return identifier");
        return execute(() -> refundRepository.findByReturnId(returnId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "The return " + returnId + " has no refund registered")));
    }

    public Refund approve(Long refundId, Long authorizerId) {
        return execute(() -> {
            userService.requireRole(authorizerId, Role.ADMINISTRATOR);
            Refund refund = transition(refundId, RefundStatus.APPROVED);

            refund.setAuthorizedById(authorizerId);
            refundRepository.update(refund);
            return refund;
        });
    }

    public Refund reject(Long refundId, Long authorizerId) {
        return execute(() -> {
            userService.requireRole(authorizerId, Role.ADMINISTRATOR);
            Refund refund = transition(refundId, RefundStatus.REJECTED);

            refund.setAuthorizedById(authorizerId);
            refundRepository.update(refund);
            return refund;
        });
    }

    public Refund markAsPaid(Long refundId) {
        return execute(() -> {
            Refund refund = transition(refundId, RefundStatus.PAID);

            refund.setPaidAt(LocalDateTime.now());
            refundRepository.update(refund);
            return refund;
        });
    }

    private Refund transition(Long refundId, RefundStatus target) {
        return execute(() -> {
            Refund refund = findById(refundId);
            require(ALLOWED_TRANSITIONS.get(refund.getStatus()).contains(target),
                    "A refund cannot move from " + refund.getStatus() + " to " + target);

            refund.setStatus(target);
            refundRepository.update(refund);
            return refund;
        });
    }
}
