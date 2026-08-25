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
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "inventarios")
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // el inventario siempre va amarrado a un producto y a una bodega
    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "variante_id")
    private Variante variante;

    @ManyToOne
    @JoinColumn(name = "bodega_id", nullable = false)
    private Bodega bodega;

    // nunca puede quedar en negativo
    @Column(nullable = false)
    private int cantidadDisponible;

    @Column(nullable = false)
    private int cantidadReservada;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoInventario estado;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;
}
