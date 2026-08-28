package application.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DetallePedido {

    private Long id;
    private Long pedidoId;  
    private Long productoId;  
    private Long varianteId;
    private String nombreProducto;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}
