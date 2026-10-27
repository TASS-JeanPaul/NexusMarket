package application.dialog.requests;

import application.data.enums.RefundMethod;

import java.math.BigDecimal;

public record RefundRequest(Long returnId, BigDecimal amount, RefundMethod method) {
}
