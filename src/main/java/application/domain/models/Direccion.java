package application.domain.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Direccion {

    private Long id;
    private Long compradorId;
    private String direccion;
    private String ciudad;
    private String departamento;
    private String observaciones;
    private boolean principal;
}
