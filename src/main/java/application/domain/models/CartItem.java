package application.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CartItem {
    private Long id;
    private Long cartId;
    private Long productId;
    private Long variantId;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
