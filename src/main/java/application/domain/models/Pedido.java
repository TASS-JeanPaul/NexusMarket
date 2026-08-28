package application.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

public class Pedido {

    private Long id;
    private String numeroPedido;
    private Comprador comprador;
    private Direccion direccionEntrega;
    private List<DetallePedido> detalles = new ArrayList<>();
    private EstadoPedido estado;
    private BigDecimal total;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaFinalizacion;
}
