package application.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "envios")
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // solo aplica para pedidos con productos fisicos
    @OneToOne
    @JoinColumn(name = "pedido_id", nullable = false, unique = true)
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "bodega_origen_id", nullable = false)
    private Bodega bodegaOrigen;

    @ManyToOne
    @JoinColumn(name = "operador_id", nullable = false)
    private Usuario operadorLogistico;

    @Column(nullable = false, unique = true, length = 40)
    private String numeroGuia;

    @Column(nullable = false, length = 100)
    private String transportadora;

    @Column(nullable = false, length = 200)
    private String direccionEntrega;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoEnvio estado;

    private LocalDateTime fechaDespacho;

    private LocalDateTime fechaEntrega;
}
