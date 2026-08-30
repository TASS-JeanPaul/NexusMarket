package application.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import application.domain.enums.EstadoEnvio;

@Getter
@Setter
public class Envio {

    private Long id;
    private Long pedidoId;
    private Long bodegaOrigenId;
    private Long operadorLogisticoId;
    private String numeroGuia;
    private String transportadora;
    private String direccionEntrega;
    private EstadoEnvio estado;
    private LocalDateTime fechaDespacho;
    private LocalDateTime fechaEntrega;
}
