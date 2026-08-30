package application.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ItemCarrito {

    private Long id;
    private Long carritoId;
    private Long productoId;
    private Long varianteId;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}
