package application.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import application.domain.enums.EstadoComercial;
import application.domain.enums.Rol;

/**
 * Un comprador es un usuario del marketplace, y ademas guarda
 * la informacion que necesita para comprar.
 */
@Getter
@Setter
public class Comprador extends Usuario {

    private EstadoComercial estadoComercial;
    private List<Direccion> direcciones = new ArrayList<>();

    public Comprador() {
        setRol(Rol.COMPRADOR);
    }
}
