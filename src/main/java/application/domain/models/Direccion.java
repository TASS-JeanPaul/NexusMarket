package application.domain.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Direccion {

    private Long id;
    private Long compradorId; // CAMBIO: De objeto 'Comprador' a su ID numérico
    private String direccion;
    private String ciudad;
    private String departamento;
    private String observaciones; // Puede ser null
    private boolean principal;
}
