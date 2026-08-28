package application.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class Factura {

  
    private Long id;
    private String numeroFactura;
    private Pedido pedido;
    private BigDecimal subtotal;
    private BigDecimal impuestos;
    private BigDecimal total;
    private EstadoFactura estado;
    private LocalDateTime fechaEmision;
}
