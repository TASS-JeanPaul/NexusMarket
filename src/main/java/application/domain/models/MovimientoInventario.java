package application.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import application.domain.enums.TipoMovimiento;

@Getter
@Setter
public class MovimientoInventario {

    private Long id;

    private Long inventarioId;
    private TipoMovimiento tipo;
    private int cantidad;
    private Long usuarioId;
    private String observacion;
    private LocalDateTime fecha;
}
