package application.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import application.domain.enums.ReturnStatus;

@Getter
@Setter
public class ProductReturn {
    private Long id;
    private String returnNumber;
    private Long orderId;
    private Long buyerId;
    private List<ProductReturnDetail> details = new ArrayList<>();
    private String reason;
    private ReturnStatus status;
    private Long reviewedById;
    private LocalDateTime requestedAt;
    private LocalDateTime closedAt;
}
