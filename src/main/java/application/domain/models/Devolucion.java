package application.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Devolucion {

    private Long id;
    private String numeroDevolucion;
    private Long pedidoId;       
    private Long compradorId;    
    private List<DetalleDevolucion> detalles = new ArrayList<>(); 
    private String motivo;
    private EstadoDevolucion estado;
    private Long revisadoPorId;  
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaCierre; 
}
