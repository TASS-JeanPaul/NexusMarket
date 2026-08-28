package application.domain.models;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import application.domain.enums.EstadoReembolso;
import application.domain.enums.MetodoReembolso;

@Getter
@Setter

public class Reembolso {

    private Long id;
    private Devolucion devolucion;
    private BigDecimal monto;
    private MetodoReembolso metodo;
    private EstadoReembolso estado;
    private Usuario autorizadoPor;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaPago;
}
