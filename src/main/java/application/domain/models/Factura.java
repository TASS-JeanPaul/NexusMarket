package application.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import application.domain.enums.EstadoFactura;

@Getter
@Setter
public class Factura {

    private Long id;
    private String numeroFactura;
    private Long pedidoId;
    private BigDecimal subtotal;
    private BigDecimal impuestos;
    private BigDecimal total;
    private EstadoFactura estado;
    private LocalDateTime fechaEmision;
}
