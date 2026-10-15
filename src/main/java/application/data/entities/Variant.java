package application.data.entities;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Variant {
    private Long id;
    private Long productId;
    private String sku;
    private String name;
    private String value;
    private BigDecimal additionalPrice;
}
