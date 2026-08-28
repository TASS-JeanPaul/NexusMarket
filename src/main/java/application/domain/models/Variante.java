package application.domain.models;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter

public class Variante {

    private Long id;
    private Producto producto;
    private String sku;
    private String nombre;
    private String valor;
    private BigDecimal precioAdicional;
}
