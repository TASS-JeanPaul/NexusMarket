package application.data.entities;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import application.data.enums.InvoiceStatus;

@Getter
@Setter
public class Invoice {
    private Long id;
    private String invoiceNumber;
    private Long orderId;
    private BigDecimal subtotal;
    private BigDecimal taxes;
    private BigDecimal total;
    private InvoiceStatus status;
    private LocalDateTime issuedAt;
}
