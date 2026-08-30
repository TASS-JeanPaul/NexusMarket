package application.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import application.domain.enums.EstadoInventario;

@Getter
@Setter
public class Inventario {

    private Long id;
    private Long productoId;
    private Long varianteId;
    private Long bodegaId;
    private int cantidadDisponible;
    private int cantidadReservada;
    private EstadoInventario estado;
    private LocalDateTime fechaActualizacion;
}
