package application.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import application.domain.enums.EstadoEnvio;

@Getter
@Setter
public class Envio {

    private Long id;
    private Pedido pedido;
    private Bodega bodegaOrigen;
    private Usuario operadorLogistico;
    private String numeroGuia;
    private String transportadora;
    private String direccionEntrega;
    private EstadoEnvio estado;
    private LocalDateTime fechaDespacho;
    private LocalDateTime fechaEntrega;
}
