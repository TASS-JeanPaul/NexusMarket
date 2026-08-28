package application.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

public class Producto {
    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private TipoProducto tipo;
    private EstadoProducto estado;
    private Vendedor vendedor;
    private Categoria categoria;
    private List<Variante> variantes = new ArrayList<>();
    private LocalDateTime fechaCreacion;
}
