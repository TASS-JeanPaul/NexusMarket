package application.domain.models;

import application.domain.enums.TipoBodega;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Bodega {

    private Long id;
    private String codigo;
    private String nombre;
    private String direccion;
    private String ciudad;
    private TipoBodega tipo; 
    private Long vendedorId;
    private boolean activa;
}
