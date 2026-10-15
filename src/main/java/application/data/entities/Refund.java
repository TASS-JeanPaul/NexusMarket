package application.data.entities;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import application.data.enums.RefundStatus;
import application.data.enums.RefundMethod;

@Getter
@Setter
public class Refund {
    private Long id;
    private Long returnId;
    private BigDecimal amount;
    private RefundMethod method;
    private RefundStatus status;
    private Long authorizedById;
    private LocalDateTime requestedAt;
    private LocalDateTime paidAt;
}
