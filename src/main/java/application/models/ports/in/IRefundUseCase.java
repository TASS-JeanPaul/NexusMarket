package application.models.ports.in;

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

public interface IRefundUseCase {

    Refund request(Long returnId, BigDecimal amount, RefundMethod method);

    Refund findById(Long id);

    List<Refund> findAll();

    Refund findByReturn(Long returnId);

    Refund approve(Long refundId, Long authorizerId);

    Refund reject(Long refundId, Long authorizerId);

    Refund markAsPaid(Long refundId);
}
