package application.domain.models;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter

public class ItemCarrito {

    private Long id;
    private Carrito carrito;
    private Producto producto;
    private Variante variante;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}
