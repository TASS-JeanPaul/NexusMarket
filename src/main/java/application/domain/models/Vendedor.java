package application.domain.models;

import lombok.Getter;
import lombok.Setter;

import application.domain.enums.Rol;

/**
 * Un vendedor es un usuario del marketplace con los datos de su empresa.
 * No se puede registrar solo: lo incorpora un administrador.
 */
@Getter
@Setter
public class Vendedor extends Usuario {

    private String razonSocial;
    private String nit;
    private Long registradoPorId;

    public Vendedor() {
        setRol(Rol.VENDEDOR);
    }
}
