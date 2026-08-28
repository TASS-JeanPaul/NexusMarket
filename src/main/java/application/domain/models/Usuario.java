package application.domain.models;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

public class Usuario {

    private Long id;
    private String nombreCompleto;
    private String documentoIdentidad;
    private String correoElectronico;
    private String telefono;
    private Rol rol;
    private EstadoUsuario estado;
    private LocalDateTime fechaRegistro;
}
