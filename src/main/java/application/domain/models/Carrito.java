package application.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Carrito {

    private Long id;
    private Long compradorId;
    private List<ItemCarrito> items = new ArrayList<>();
    private BigDecimal total;
    private boolean activo;
    private LocalDateTime fechaCreacion;
}
