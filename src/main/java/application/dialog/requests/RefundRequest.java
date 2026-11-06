package application.dialog.requests;

import application.data.enums.RefundMethod;

import java.math.BigDecimal;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record RefundRequest(@NotNull Long returnId, @NotNull @Positive BigDecimal amount, @NotNull RefundMethod method) {
}
