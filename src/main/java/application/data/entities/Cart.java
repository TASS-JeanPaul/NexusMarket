package application.data.entities;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Cart {
    private Long id;
    private Long buyerId;
    private List<CartItem> items = new ArrayList<>();
    private BigDecimal total;
    private boolean active;
    private LocalDateTime createdAt;
}
