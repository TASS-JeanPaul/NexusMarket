package application.domain.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetalleDevolucion {

    private Long id;
    private Long devolucionId;
    private Long detallePedidoId;
    private int cantidadDevuelta;
    private String observacion;
}
