package application.domain.models;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import application.domain.enums.EstadoInventario;

@Getter
@Setter

public class Inventario {

    private Long id;
    private Producto producto;
    private Variante variante;
    private Bodega bodega;
    private int cantidadDisponible;
    private int cantidadReservada;
    private EstadoInventario estado;
    private LocalDateTime fechaActualizacion;
}
