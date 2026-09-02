package application.domain.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductReturnDetail {
    private Long id;
    private Long returnId;
    private Long orderDetailId;
    private int returnedQuantity;
    private String notes;
}
