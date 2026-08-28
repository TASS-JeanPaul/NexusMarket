package application.domain.models;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import application.domain.enums.EstadoVendedor;

@Getter
@Setter

public class Vendedor {

    private Long id;
    private Usuario usuario;
    private String razonSocial;
    private String nit;
    private String telefonoContacto;
    private EstadoVendedor estado;
    private Usuario registradoPor;
    private LocalDateTime fechaRegistro;
}
