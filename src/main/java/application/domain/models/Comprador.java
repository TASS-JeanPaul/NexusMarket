package application.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Comprador {

    private Long id;
    private Long usuarioId; 
    private EstadoComercial estadoComercial; 
    private LocalDateTime fechaRegistro;
    private List<Direccion> direcciones = new ArrayList<>(); 
}
