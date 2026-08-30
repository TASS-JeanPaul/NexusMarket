package application.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import application.domain.enums.EstadoProducto;
import application.domain.enums.TipoProducto;

/**
 * Producto del catalogo.
 * Es abstracta porque el negocio no vende "productos" en general:
 * vende productos fisicos o productos digitales, y cada uno se comporta distinto.
 */
@Getter
@Setter
public abstract class Producto {

    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private EstadoProducto estado;
    private Long vendedorId;
    private Long categoriaId;
    private List<Variante> variantes = new ArrayList<>();
    private LocalDateTime fechaCreacion;

    // cada hijo dice que tipo es
    public abstract TipoProducto getTipo();

    // el fisico se despacha, el digital se entrega apenas se paga
    public abstract boolean requiereEnvio();

    // el fisico descuenta existencias, el digital no maneja inventario
    public abstract boolean manejaInventario();
}
